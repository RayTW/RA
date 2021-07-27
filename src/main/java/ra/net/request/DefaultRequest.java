package ra.net.request;

import org.json.JSONObject;
import ra.net.NetService;
import ra.net.Sendable;

/**
 * Default request.
 *
 * @author Ray Li
 */
public class DefaultRequest extends Request {
  private String command;

  private String authorization;

  private String source;

  private JSONObject json;

  private long reciveTimestamp;

  private Sendable<String> sender;

  /**
   * Initialize.
   *
   * @param request request
   */
  public DefaultRequest(NetService.NetRequest request) {
    super(request);

    this.sender = request.getSender();
    this.reciveTimestamp = System.currentTimeMillis();
    this.source = request.getText();
    this.json = new JSONObject(source);
    this.command = json.optString("command", null);
    this.authorization = json.optString("authorization", null);
  }

  /**
   * Returns command.
   *
   * @return command
   */
  public String getCommand() {
    return command;
  }

  /**
   * Set command of the request.
   *
   * @param command command
   */
  public void setCommand(String command) {
    this.command = command;
  }

  /**
   * Returns receive time of the request.
   *
   * @return millisecond
   */
  public long getReciveTime() {
    return reciveTimestamp;
  }

  /**
   * Set receive time of the request.
   *
   * @param timestamp millisecond
   */
  public void setReciveTime(long timestamp) {
    reciveTimestamp = timestamp;
  }

  /**
   * Returns authorization of the request.
   *
   * @return String
   */
  public String getAuthorization() {
    return authorization;
  }

  /**
   * Set authorization of the request.
   *
   * @param authorization authorization
   */
  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }

  /**
   * Returns source text of the request.
   *
   * @return String
   */
  public String getSource() {
    return source;
  }

  /**
   * Set source text of the request.
   *
   * @param source source
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * Returns JSONObject of the request.
   *
   * @return JSONObject
   */
  public JSONObject getJson() {
    return json;
  }

  /**
   * Set JSONObject of the request.
   *
   * @param json json
   */
  public void setJson(JSONObject json) {
    this.json = json;
  }

  /**
   * Send message as a response.
   *
   * @param message message
   */
  public void send(String message) {
    this.sender.send(message);
  }

  /**
   * Send message as a response and close connection.
   *
   * @param message message
   */
  public void sendClose(String message) {
    this.sender.sendClose(message);
  }
}
