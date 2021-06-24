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
 * 驗證每個Authorization只能呼叫指定的API command.
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

  /** key:部門單位名稱, value:部門access token. */
  private static Map<String, String> accessTokens = new ConcurrentHashMap<>();
  /** key:API command, value:部門單位名稱. */
  private static Map<String, List<String>> commandsAuthorization = new ConcurrentHashMap<>();

  /**
   * 戴入各API command援權的access token與部門名稱.
   *
   * @param path 設定檔路徑
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
   * 驗證是否有通過授權.
   *
   * @param command Client傳送的command
   * @param auth Client傳送的Auth Key
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
   * 取得各部門單位的access token.
   *
   * @return key:部門單位名稱, value:部門access token
   */
  public static Map<String, String> getAccessTokens() {
    return accessTokens;
  }

  /**
   * 取得各command對應可讀取的部門單位.
   *
   * @return key:API command, value:部門單位名稱
   */
  public static Map<String, List<String>> getCommandsAuthorization() {
    return commandsAuthorization;
  }
}
