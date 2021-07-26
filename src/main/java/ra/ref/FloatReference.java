package ra.ref;

/**
 * Float type of reference..
 *
 * @author Ray Li
 */
public class FloatReference {
  private float value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public FloatReference(float value) {
    this.value = value;
  }

  /** Initialize. */
  public FloatReference() {}

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
  public void set(float value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
