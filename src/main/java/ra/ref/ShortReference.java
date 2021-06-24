package ra.ref;

/**
 * Short type of refercnce.
 *
 * @author Ray Li
 */
public class ShortReference {
  private short value;

  public ShortReference(short value) {
    this.value = value;
  }

  public ShortReference() {}

  public short get() {
    return value;
  }

  public void set(short value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
