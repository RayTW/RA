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

  /**
   * initialize.
   *
   * @param index index
   * @param send send
   */
  public Response(int index, SenderAdapter send) {
    sendAdapter = send;
    this.index = index;
  }

  /**
   * Return index.
   *
   * @return index
   */
  public int getIndex() {
    return index;
  }

  /**
   * Send message.
   *
   * @param message message
   */
  public void send(String message) {
    sendAdapter.send(message, index);
  }

  /**
   * Send message.
   *
   * @param json message
   */
  public void send(JSONObject json) {
    sendAdapter.send(json, index);
  }

  /**
   * Send message.
   *
   * @param json message
   */
  public void send(JSONArray json) {
    sendAdapter.send(json.toString(), index);
  }

  /**
   * Send message and close connection.
   *
   * @param json message
   */
  public void sendClose(JSONArray json) {
    sendAdapter.sendClose(json.toString(), index);
  }

  /**
   * Send message and close connection.
   *
   * @param message message
   */
  public void sendClose(String message) {
    sendAdapter.sendClose(message, index);
  }

  /**
   * Send message and close connection.
   *
   * @param json message
   */
  public void sendClose(JSONObject json) {
    sendAdapter.sendClose(json, index);
  }

  /**
   * Send message and record log.
   *
   * @param request request
   * @param code code
   * @param message message
   */
  public void sendError(String request, int code, String message) {
    sendAdapter.sendError(request, code, message, index);
  }

  /**
   * Disconnect the client connection after sending the error message and record logging.
   *
   * @param request request
   * @param code error code
   * @param message error message
   */
  public void sendErrorClose(String request, int code, String message) {
    sendAdapter.sendErrorClose(request, code, message, index);
  }
}
