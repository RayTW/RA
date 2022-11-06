package ra.db;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import ra.ref.BiReference;

/**
 * Parameter value.
 *
 * @author Ray Li
 */
public class ParameterValue {
  private Class<?> type;
  private Object value;

  public Class<?> getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return type + ", value=" + value;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Creates a {@code ParameterValue} object with the given value and type.
   *
   * @param <T> T
   * @param value value value
   * @param type type type
   * @return ParameterValue
   */
  public static <T> ParameterValue of(T value, Class<?> type) {
    return ParameterValue.newBuilder().setType(type).setValue(value).build();
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Boolean.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue bool(Boolean value) {
    return of(value, Boolean.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of String.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue string(String value) {
    return of(value, String.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Long.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue int64(Long value) {
    return of(value, Long.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Integer.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue int64(Integer value) {
    return of(value, Integer.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Double.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue float64(Double value) {
    return of(value, Double.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Float.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue float64(Float value) {
    return of(value, Float.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of BigDecimal.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue bigNumeric(BigDecimal value) {
    return of(value, BigDecimal.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of byte[].
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue bytes(byte[] value) {
    return of(value, byte[].class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Blob.
   *
   * @param value value
   * @return ParameterValue
   */
  public static ParameterValue blob(Blob value) {
    return of(value, Blob.class);
  }

  /**
   * Creates a {@code ParameterValue} object with a type of Array.
   *
   * @param typeName value type
   * @param elements value
   * @return ParameterValue
   */
  public static ParameterValue array(String typeName, Object[] elements) {
    return of(new BiReference<>(typeName, elements), Array.class);
  }

  /** Builder. */
  public static final class Builder {
    private Class<?> type;
    private Object value;

    public Builder setValue(Object value) {
      this.value = value;
      return this;
    }

    public Builder setType(Class<?> type) {
      this.type = type;
      return this;
    }

    /**
     * Build.
     *
     * @return ParameterValue
     */
    public ParameterValue build() {
      ParameterValue obj = new ParameterValue();

      if (type == null) {
        throw new NullPointerException("type is required.");
      }

      obj.type = type;
      obj.value = value;

      return obj;
    }
  }
}
