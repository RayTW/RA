package ra.ref;

/**
 * Integer type of reference.
 *
 * @author Ray Li
 */
public class IntReference {
  private int value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public IntReference(int value) {
    this.value = value;
  }

  /** Initialize. */
  public IntReference() {}

  /**
   * Returns the value.
   *
   * @return value
   */
  public int get() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value value
   */
  public void set(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
