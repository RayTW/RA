package ra.net.processor;

import ra.net.request.Request;

/**
 * Provide CommandProcessor.
 *
 * @author Ray Li, Kevin Tsai
 */
public interface CommandProcessorProvider<T extends Request> {

  /**
   * Create {@link CommandProcessorListener}.
   *
   * @return CmdProcListener
   */
  public abstract CommandProcessorListener<T> createCommand();

  /**
   * Offline event.
   *
   * @param index index
   */
  public abstract void offline(int index);
}
