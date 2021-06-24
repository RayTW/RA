package ra.ref;

/**
 * Boolean type of reference.
 *
 * @author Ray Li
 */
public class BooleanReference {
  private boolean value;

  public BooleanReference(boolean value) {
    this.value = value;
  }

  public BooleanReference() {}

  public boolean get() {
    return value;
  }

  public void set(boolean value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
