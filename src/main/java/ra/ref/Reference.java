package ra.ref;

/**
 * Any type of Reference.
 *
 * @author Ray Li
 */
public class Reference<T> {
  private T value;

  public Reference(T value) {
    this.value = value;
  }

  public Reference() {}

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }

  public boolean isNull() {
    return value == null;
  }

  public boolean isNotNull() {
    return value != null;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
