package ra.ref;

/**
 * Integer type of reference.
 *
 * @author Ray Li
 */
public class IntReference {
  private int value;

  public IntReference(int value) {
    this.value = value;
  }

  public IntReference() {}

  public int get() {
    return value;
  }

  public void set(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
