package ra.net.processor;

import ra.net.request.Request;

/**
 * Process string command.
 *
 * @author Ray Li, Kevin Tsai
 */
public interface CommandProcessorListener<T> {
  /**
   * Received message.
   *
   * @param request request
   */
  public abstract void commandProcess(Request<T> request);
}
