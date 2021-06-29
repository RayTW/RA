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

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public long getReciveTime() {
    return reciveTimestamp;
  }

  public void setReciveTime(long timestamp) {
    reciveTimestamp = timestamp;
  }

  public String getAuthorization() {
    return authorization;
  }

  public void getAuthorization(String authorization) {
    this.authorization = authorization;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public JSONObject getJson() {
    return json;
  }

  public void setJson(JSONObject json) {
    this.json = json;
  }

  public void send(String message) {
    this.sender.send(message);
  }

  public void sendClose(String message) {
    this.sender.sendClose(message);
  }
}
