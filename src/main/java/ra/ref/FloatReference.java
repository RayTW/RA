package ra.ref;

/**
 * Float type of reference..
 *
 * @author Ray Li
 */
public class FloatReference {
  private float value;

  public FloatReference(float value) {
    this.value = value;
  }

  public FloatReference() {}

  public double get() {
    return value;
  }

  public void set(float value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
