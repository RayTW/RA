package ra.ref;

/**
 * Short type of refercnce.
 *
 * @author Ray Li
 */
public class ShortReference {
  private short value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public ShortReference(short value) {
    this.value = value;
  }

  /** Initialize. */
  public ShortReference() {}

  /**
   * Returns the value.
   *
   * @return value
   */
  public short get() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value value
   */
  public void set(short value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
