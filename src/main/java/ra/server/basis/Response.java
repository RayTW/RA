package ra.server.basis;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Response.
 *
 * @author Ray Li
 */
public class Response {
  private int index;
  private SenderAdapter sendAdapter;

  public Response(int index, SenderAdapter send) {
    sendAdapter = send;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public void send(String obj) {
    sendAdapter.send(obj, index);
  }

  public void send(JSONObject json) {
    sendAdapter.send(json, index);
  }

  public void send(JSONArray json) {
    sendAdapter.send(json.toString(), index);
  }

  public void sendClose(JSONArray json) {
    sendAdapter.sendClose(json.toString(), index);
  }

  public void sendClose(String obj) {
    sendAdapter.sendClose(obj, index);
  }

  public void sendClose(JSONObject json) {
    sendAdapter.sendClose(json, index);
  }

  public void sendError(String request, int code, String message) {
    sendAdapter.sendError(request, code, message, index);
  }

  /**
   * Disconnect the client connection after sending the error message and record logging.
   *
   * @param request 收到的request內容
   * @param code error code
   * @param message error message
   */
  public void sendErrorClose(String request, int code, String message) {
    sendAdapter.sendErrorClose(request, code, message, index);
  }
}
