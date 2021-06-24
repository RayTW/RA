package ra.net.request;

import org.json.JSONObject;

/**
 * Default request.
 *
 * @author Ray Li
 */
public class DefaultRequest extends Request<String> {
  private String command;

  private String authorization;

  private String source;

  private JSONObject json;

  private long reciveTimestamp;

  private int index;

  private String ipAddress;

  /**
   * Initialize.
   *
   * @param request request
   */
  public DefaultRequest(Request<String> request) {
    super(request);

    this.reciveTimestamp = System.currentTimeMillis();
    this.source = new String(request.getDataBytes());
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

  @Override
  public int getIndex() {
    return index;
  }

  public String getIpAddress() {
    return ipAddress;
  }
}
