package ra.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Since Java's primitive types cannot accurately perform operations on floating-point numbers, this
 * tool class provides accurate floating-point operations, including addition, subtraction,
 * multiplication, division, and rounding..
 */
public class Arith {
  // Default division precision.
  private static final int DEF_DIV_SCALE = 10;

  private Arith() {}

  /**
   * Returns sum two values.
   *
   * @param v1 value1
   * @param v2 value2
   * @return Returns sum two values.
   */
  public static double add(double v1, double v2) {
    BigDecimal b1 = new BigDecimal(Double.toString(v1));
    BigDecimal b2 = new BigDecimal(Double.toString(v2));
    return b1.add(b2).doubleValue();
  }

  /**
   * Returns the subtraction of two values.
   *
   * @param v1 value1
   * @param v2 value2
   * @return Returns the subtraction of two values.
   */
  public static double sub(double v1, double v2) {
    BigDecimal b1 = new BigDecimal(Double.toString(v1));
    BigDecimal b2 = new BigDecimal(Double.toString(v2));
    return b1.subtract(b2).doubleValue();
  }

  /**
   * Provide precise multiplication operations.
   *
   * @param v1 value1
   * @param v2 value2
   * @return Product of two parameters
   */
  public static double mul(double v1, double v2) {
    BigDecimal b1 = new BigDecimal(Double.toString(v1));
    BigDecimal b2 = new BigDecimal(Double.toString(v2));
    return b1.multiply(b2).doubleValue();
  }

  /**
   * Provides (relatively) accurate division operations. When the division is inexhaustible, the
   * accuracy is 10 digits after the decimal point, and subsequent numbers are rounded.
   *
   * @param v1 value1
   * @param v2 value2
   * @return quotient of two parameters
   */
  public static double div(double v1, double v2) {
    return div(v1, v2, DEF_DIV_SCALE);
  }

  /**
   * Provides (relatively) precise division operations. When the division is inexhaustible, the
   * precision is specified by the scale parameter, and the subsequent figures are rounded.
   *
   * @param v1 value1
   * @param v2 value2
   * @param scale Indicates that it needs to be accurate to a few decimal places.
   * @return quotient of two parameters
   */
  public static double div(double v1, double v2, int scale) {
    if (scale < 0) {
      throw new IllegalArgumentException("The scale must be a positive integer or zero");
    }
    BigDecimal b1 = new BigDecimal(Double.toString(v1));
    BigDecimal b2 = new BigDecimal(Double.toString(v2));
    return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
  }

  /**
   * Provide accurate decimal rounding processing.
   *
   * @param v value
   * @param scale keep a few digits after the decimal point
   * @return rounded result
   */
  public static double round(double v, int scale) {
    if (scale < 0) {
      throw new IllegalArgumentException("The scale must be a positive integer or zero");
    }
    BigDecimal b = new BigDecimal(Double.toString(v));
    BigDecimal one = new BigDecimal("1");
    return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
  }

  /**
   * Provide accurate decimal places to be discarded unconditionally.
   *
   * @param value value
   * @param scale scale
   * @return the largest (closest to positive infinity) floating-point value that less than or equal
   *     to the argument and is equal to a mathematical integer..
   */
  public static double floor(double value, int scale) {
    if (scale < 0) {
      throw new IllegalArgumentException("The scale must be a positive integer or zero");
    }
    BigDecimal b = new BigDecimal(Double.toString(value));
    BigDecimal one = new BigDecimal("1");
    return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
  }

  /**
   * Returns double value convert to plain string.
   *
   * @param v value
   * @return string
   */
  public static String toPlainString(double v) {
    BigDecimal c = new BigDecimal(String.valueOf(v));
    return c.toPlainString();
  }
}
