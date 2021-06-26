package ra.server.basis;

import java.util.Map;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;
import ra.net.MessageSender;
import ra.net.UserListener;
import ra.util.logging.LogEveryDay;

/**
 * Message sender.
 *
 * @author Ray Li , Kevin Tsai
 */
public class SenderAdapter extends MessageSender {
  private static final String LOG_TAG_RESPONSE = "response";
  private MessageSender sender;
  private Optional<LogEveryDay> commonLog;
  private Optional<LogEveryDay> errorLog;

  /**
   * Not record log.
   *
   * @param send send
   */
  public SenderAdapter(MessageSender send) {
    this(send, null, null);
  }

  /**
   * If argument 'commonLog' and 'errorLog' not null will enable logging.
   *
   * @param sender sender
   * @param commonLog commonLog
   * @param errorLog errorLog
   */
  public SenderAdapter(MessageSender sender, LogEveryDay commonLog, LogEveryDay errorLog) {
    this.commonLog = Optional.ofNullable(commonLog);
    this.errorLog = Optional.ofNullable(errorLog);
    this.sender = sender;
  }

  @Override
  public void broadcast(String message) {
    sender.broadcast(message);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, message));
  }

  @Override
  public <T extends UserListener> void broadcast(String message, Map<String, T> userlist) {
    sender.broadcast(message, userlist);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, message));
  }

  @Override
  public void send(String message, int index) {
    sender.send(message, index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, message, index));
  }

  public void send(JSONObject json, int index) {
    sender.send(json.toString(), index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, "" + json, index));
  }

  @Override
  public void sendClose(String message, int index) {
    sender.sendClose(message, index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, message, index));
  }

  public void sendClose(JSONObject json, int index) {
    sender.sendClose(json.toString(), index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, "" + json, index));
  }

  /**
   * Logging after sending error message.
   *
   * @param request request
   * @param code error code
   * @param message message
   * @param index target index
   */
  public void sendError(String request, int code, String message, int index) {
    JSONObject resJson = new JSONObject();
    try {
      resJson.put("code", code);

      if (message != null) {
        resJson.put("message", message);
      }
      String ret = resJson.toString();

      send(ret, index);
      errorLog.ifPresent(log -> log.writeln("request[" + request + "],response[" + ret + "]"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Logging after sending error message and close connection.
   *
   * @param request request
   * @param code error code
   * @param message message
   * @param index target index
   */
  public void sendErrorClose(String request, int code, String message, int index) {
    JSONObject resJson = new JSONObject();
    try {
      resJson.put("code", code);

      if (message != null) {
        resJson.put("message", message);
      }
      String ret = resJson.toString();

      sendClose(ret, index);
      errorLog.ifPresent(log -> log.writeln("request[" + request + "],response[" + ret + "]"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
