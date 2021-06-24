package ra.net.processor;

/**
 * Provide CommandProcessor.
 *
 * @author Ray Li, Kevin Tsai
 */
public interface CommandProcessorProvider<T> {

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
