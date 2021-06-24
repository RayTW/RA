package ra.server.basis;

import org.json.JSONObject;
import ra.net.request.DefaultRequest;

/**
 * The server received event from a client request.
 *
 * @author Ray Li
 */
public interface OnRequestListener {

  /**
   * Received request.
   *
   * @param request request
   */
  public void onRequest(DefaultRequest request);

  /**
   * Heart beat.
   *
   * @param json Json package
   * @param index Request index
   */
  public void onHeartbeat(JSONObject json, int index);

  /**
   * Monitor information.
   *
   * @param json Json package
   * @param index Request index
   */
  public void onMonitor(JSONObject json, int index);

  /**
   * When Mismatch command will invoke {@link #onUnkonwException(String, String, int)}.
   *
   * @param source Json format
   * @param message message
   * @param index Request index
   */
  public void onUnkonwException(String source, String message, int index);

  /**
   * When Mismatch command will invoke {@link #onUnkonwException(String, String, int)}.
   *
   * @param source Json format
   * @param index Request index
   */
  public void onCommandNotFound(String source, int index);

  /**
   * Invalid request or verify authentication failure.
   *
   * @param source Json format
   * @param index Request index
   */
  public void onAuthenticationFailure(String source, int index);
}
