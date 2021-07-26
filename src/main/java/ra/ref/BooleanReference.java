package ra.ref;

/**
 * Boolean type of reference.
 *
 * @author Ray Li
 */
public class BooleanReference {
  private boolean value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public BooleanReference(boolean value) {
    this.value = value;
  }

  /** Initialize. */
  public BooleanReference() {}

  /**
   * Returns the value.
   *
   * @return value
   */
  public boolean get() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value value
   */
  public void set(boolean value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
