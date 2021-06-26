package ra.net;

/**
 * Communication processing layer.
 *
 * @author Ray Li
 */
public interface NetServiceable extends Serviceable<String> {
  /**
   * Returns true will encryption.
   *
   * @return enable enable
   */
  public abstract boolean getSendcompilete();

  /**
   * Enable encryption.
   *
   * @param encryption enable encryption
   */
  public abstract void setSendcompilete(boolean encryption);
}
