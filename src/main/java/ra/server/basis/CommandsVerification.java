package ra.server.basis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Verify command mapping authorization.
 *
 * @author Ray Li
 */
public class CommandsVerification {
  /** authorization F5. */
  public static final String AUTHORIZATION_F5 = "F5";

  /** authorization Monitor. */
  public static final String AUTHORIZATION_MONITOR = "Monitor";

  /** command heart beat. */
  public static final String HEARTBEAT = "/v1/server/heartbeat";

  /** command monitor. */
  public static final String MONITOR = "/v1/server/monitor";

  /** key:depart, value:department access token. */
  private static Map<String, String> accessTokens = new ConcurrentHashMap<>();
  /** key:API command, value: department name. */
  private static Map<String, List<String>> commandsAuthorization = new ConcurrentHashMap<>();

  /**
   * Load commands.
   *
   * @param path path
   * @throws IOException path
   */
  public static void loadCommands(String path) throws IOException {
    String text = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    JSONObject json = new JSONObject(text);
    JSONArray tokens = json.getJSONArray("accessTokens");
    JSONArray authorization = json.getJSONArray("commandsAuthorization");

    StreamSupport.stream(tokens.spliterator(), true)
        .map(JSONObject.class::cast)
        .forEach(
            o -> {
              accessTokens.put(o.getString("accessToken"), o.getString("department"));
            });

    StreamSupport.stream(authorization.spliterator(), true)
        .map(JSONObject.class::cast)
        .forEach(
            o -> {
              List<String> authDepartmentName = new CopyOnWriteArrayList<>();

              StreamSupport.stream(o.getJSONArray("department").spliterator(), true)
                  .map(String.class::cast)
                  .forEach(authDepartmentName::add);
              commandsAuthorization.put(o.getString("command"), authDepartmentName);
            });
  }

  /**
   * Verify command and authorization.
   *
   * @param command command
   * @param auth authorization
   * @return If command and authorization are valid returns true.
   */
  public static boolean isValidAuth(String command, String auth) {
    if (auth == null) {
      return false;
    }
    if (command == null) {
      throw new CommandNotFoundException("command '" + command + "' not found");
    }
    String departmentName = accessTokens.get(auth);

    if (departmentName != null) {
      List<String> authorizationDepartmentName = commandsAuthorization.get(command);

      if (authorizationDepartmentName == null) {
        throw new CommandNotFoundException("command '" + command + "' not found");
      }

      if (authorizationDepartmentName.contains(departmentName)) {
        return true;
      }
    }

    switch (auth) {
      case AUTHORIZATION_F5:
        switch (command) {
          case HEARTBEAT:
            return true;
          default:
            throw new CommandNotFoundException("command '" + command + "' not found");
        }
      case AUTHORIZATION_MONITOR:
        switch (command) {
          case MONITOR:
            return true;
          default:
            throw new CommandNotFoundException("command '" + command + "' not found");
        }
      default:
        break;
    }
    return false;
  }

  /**
   * Returns content size that access token of department.
   *
   * @return access token size
   */
  public static int getAccessTokensSize() {
    return accessTokens.size();
  }

  /**
   * Returns access token of department.
   *
   * @return key:department name, value:departmentaccess token
   */
  public static Stream<Entry<String, String>> getAccessTokensStream() {
    return accessTokens.entrySet().stream();
  }

  /**
   * Returns list size of departments.
   *
   * @return key:command, value:department name
   */
  public static int getCommandsAuthorizationSize() {
    return commandsAuthorization.size();
  }

  /**
   * Returns department list.
   *
   * @return key:command, value:department name
   */
  public static Stream<Entry<String, List<String>>> getCommandsAuthorizationStream() {
    return commandsAuthorization.entrySet().stream();
  }

  /** Clear all access token and commands. */
  public static void clear() {
    accessTokens.clear();
    commandsAuthorization.clear();
  }
}
