package ra.ref;

/**
 * Long type of reference.
 *
 * @author Ray Li
 */
public class LongReference {
  private long value;

  public LongReference(long value) {
    this.value = value;
  }

  public LongReference() {}

  public long get() {
    return value;
  }

  public void set(long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
