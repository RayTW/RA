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
  private MessageSender send;
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
   * 若有給參數commonLog、errorLog且不為null，則會記錄log.
   *
   * @param send 發送訊息的元件
   * @param commonLog 記錄通用log
   * @param errorLog 記錄錯誤log
   */
  public SenderAdapter(MessageSender send, LogEveryDay commonLog, LogEveryDay errorLog) {
    this.commonLog = Optional.ofNullable(commonLog);
    this.errorLog = Optional.ofNullable(errorLog);
    this.send = send;
  }

  @Override
  public void boardcast(String obj) {
    send.boardcast(obj);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, obj));
  }

  @Override
  public <T extends UserListener> void boardcast(String obj, Map<String, T> userlist) {
    send.boardcast(obj, userlist);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, obj));
  }

  @Override
  public void send(String obj, int index) {
    send.send(obj, index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, obj, index));
  }

  public void send(JSONObject json, int index) {
    send.send(json.toString(), index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, "" + json, index));
  }

  @Override
  public void sendClose(String obj, int index) {
    send.sendClose(obj, index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, obj, index));
  }

  public void sendClose(JSONObject json, int index) {
    send.sendClose(json.toString(), index);
    commonLog.ifPresent(log -> log.writeln(LOG_TAG_RESPONSE, "" + json, index));
  }

  /**
   * 記錄錯誤的request log，發送錯誤訊息.
   *
   * @param request 收到的request內容
   * @param code 錯誤的code碼
   * @param message 錯誤訊息
   * @param index 要發送對象的索引值
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
   * 記錄錯誤的request log，發送錯誤訊息並斷開client連線.
   *
   * @param request 收到的request內容
   * @param code 錯誤的code碼
   * @param message 錯誤訊息
   * @param index 要發送對象的索引值
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
