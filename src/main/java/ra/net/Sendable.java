package ra.net;

/**
 * Sendable.
 *
 * @author Ray Li
 */
public interface Sendable<T> {
  /**
   * Send message.
   *
   * @param message message
   */
  public abstract void send(T message);

  /**
   * Close connection after sent the message.
   *
   * @param message message
   */
  public abstract void sendClose(T message);
}
