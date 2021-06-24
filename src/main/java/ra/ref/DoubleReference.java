package ra.ref;

/**
 * Double type of reference.
 *
 * @author Ray Li
 */
public class DoubleReference {
  private double value;

  public DoubleReference(double value) {
    this.value = value;
  }

  public DoubleReference() {}

  public double get() {
    return value;
  }

  public void set(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
