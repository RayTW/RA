package ra.server.basis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Verify command mapping authorization.
 *
 * @author Ray Li
 */
public class CommandsVerification {
  // AUTHORIZATION
  public static final String AUTHORIZATION_F5 = "F5";
  public static final String AUTHORIZATION_MONITOR = "Monitor";

  // AUTHORIZATION_F5
  public static final String PING = "/v1/server/ping";

  // AUTHORIZATION_MONITOR
  public static final String MONITOR = "/v1/server/monitor";

  /** key:depart, value:department access token. */
  private static Map<String, String> accessTokens = new ConcurrentHashMap<>();
  /** key:API command, value:部門單位名稱. */
  private static Map<String, List<String>> commandsAuthorization = new ConcurrentHashMap<>();

  /**
   * Initialize.
   *
   * @param path path
   */
  public static void loadCommands(String path) {
    try {
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
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
          case PING:
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
   * Returns access token of department.
   *
   * @return key:department name, value:departmentaccess token
   */
  public static Map<String, String> getAccessTokens() {
    return accessTokens;
  }

  /**
   * Returns department list.
   *
   * @return key:command, value:department name
   */
  public static Map<String, List<String>> getCommandsAuthorization() {
    return commandsAuthorization;
  }
}
