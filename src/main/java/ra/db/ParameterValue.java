package ra.db;

import java.math.BigDecimal;
import java.sql.Blob;

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

  /** Creates a {@code ParameterValue} object with the given value and type. */
  public static <T> ParameterValue of(T value, Class<?> type) {
    return ParameterValue.newBuilder().setType(type).setValue(value).build();
  }

  /** Creates a {@code ParameterValue} object with a type of Boolean. */
  public static ParameterValue bool(Boolean value) {
    return of(value, Boolean.class);
  }

  /** Creates a {@code ParameterValue} object with a type of String. */
  public static ParameterValue string(String value) {
    return of(value, String.class);
  }

  /** Creates a {@code ParameterValue} object with a type of Long. */
  public static ParameterValue int64(Long value) {
    return of(value, Long.class);
  }

  /** Creates a {@code ParameterValue} object with a type of Integer. */
  public static ParameterValue int64(Integer value) {
    return of(value, Integer.class);
  }

  /** Creates a {@code ParameterValue} object with a type of Double. */
  public static ParameterValue float64(Double value) {
    return of(value, Double.class);
  }

  /** Creates a {@code ParameterValue} object with a type of Float. */
  public static ParameterValue float64(Float value) {
    return of(value, Float.class);
  }

  /** Creates a {@code ParameterValue} object with a type of BigDecimal. */
  public static ParameterValue bigNumeric(BigDecimal value) {
    return of(value, BigDecimal.class);
  }

  /** Creates a {@code ParameterValue} object with a type of byte[]. */
  public static ParameterValue bytes(byte[] value) {
    return of(value, byte[].class);
  }

  /** Creates a {@code ParameterValue} object with a type of Blob. */
  public static ParameterValue blob(Blob value) {
    return of(value, Blob.class);
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
