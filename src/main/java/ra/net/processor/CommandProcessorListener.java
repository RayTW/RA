package ra.net.processor;

import ra.net.request.Request;

/**
 * Process string command.
 *
 * @author Ray Li, Kevin Tsai
 * @param <T> request
 */
public interface CommandProcessorListener<T extends Request> {
  /**
   * Received message.
   *
   * @param request request
   */
  public abstract void commandProcess(T request);
}
