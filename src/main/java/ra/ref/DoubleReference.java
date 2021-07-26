package ra.ref;

/**
 * Double type of reference.
 *
 * @author Ray Li
 */
public class DoubleReference {
  private double value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public DoubleReference(double value) {
    this.value = value;
  }

  /** Initialize. */
  public DoubleReference() {}

  /**
   * Returns the value.
   *
   * @return value
   */
  public double get() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value value
   */
  public void set(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
