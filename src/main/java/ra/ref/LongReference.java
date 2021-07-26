package ra.ref;

/**
 * Long type of reference.
 *
 * @author Ray Li
 */
public class LongReference {
  private long value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public LongReference(long value) {
    this.value = value;
  }

  /** Initialize. */
  public LongReference() {}

  /**
   * Returns the value.
   *
   * @return value
   */
  public long get() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value value
   */
  public void set(long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
