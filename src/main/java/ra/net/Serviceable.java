package ra.net;

/**
 * The service provide send message.
 *
 * @author Ray Li
 */
public interface Serviceable<T> extends Sendable<T> {

  /** Close connection event. */
  public abstract void onClose();
}
