package ra.ref;

/**
 * Any type of Reference.
 *
 * @author Ray Li
 */
public class Reference<T> {
  private T value;

  /**
   * Initialize.
   *
   * @param value value
   */
  public Reference(T value) {
    this.value = value;
  }

  /** Initialize. */
  public Reference() {}

  /**
   * Returns the value.
   *
   * @return value
   */
  public T get() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value value
   */
  public void set(T value) {
    this.value = value;
  }

  /**
   * Returns the value whether is null.
   *
   * @return value is null.
   */
  public boolean isNull() {
    return value == null;
  }

  /**
   * Returns the value whether not null.
   *
   * @return value not null.
   */
  public boolean isNotNull() {
    return value != null;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
