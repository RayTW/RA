package ra.db;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Row of query result.
 *
 * @author Ray Li
 */
public class Row implements RowSet {
  private Map<String, byte[]> data;

  /** Initialize. */
  public Row() {
    data = new HashMap<>();
  }

  /**
   * Put the value specifies key in single rows.
   *
   * @param key column name
   * @param value data
   */
  public void put(String key, byte[] value) {
    data.put(key, value);
  }

  /**
   * Take the value of that column`s value as a byte array by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a byte array.
   */
  @Override
  public byte[] getBlob(String columnName) {
    return data.get(columnName);
  }

  /**
   * Take the value of that column`s value as a String by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a String.
   */
  @Override
  public String getString(String columnName) {
    try {
      byte[] v = data.get(columnName);

      return (v == null) ? "" : new String(v);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Take the value of that column`s value as a short by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a short.
   */
  @Override
  public short getShort(String columnName) {
    String v = getString(columnName);

    return Short.parseShort(v);
  }

  /**
   * Take the value of that column`s value as a int by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as an int.
   */
  @Override
  public int getInt(String columnName) {
    String v = getString(columnName);

    return Integer.parseInt(v);
  }

  /**
   * Take the value of that column`s value as a long by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a long.
   */
  @Override
  public long getLong(String columnName) {
    String v = getString(columnName);

    return Long.parseLong(v);
  }

  /**
   * Take the value of that column`s value as a float by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a float.
   */
  @Override
  public float getFloat(String columnName) {
    String v = getString(columnName);

    return Float.parseFloat(v);
  }

  /**
   * Take the value of that column`s value as a double by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a double.
   */
  @Override
  public double getDouble(String columnName) {
    String v = getString(columnName);

    return Double.parseDouble(v);
  }

  /**
   * Take the value of that column`s value as a double by the column`s name. Using the BigDecimal
   * transform the double value.
   *
   * @param columnName column`s name
   * @return the value of that column as a {@link BigDecimal#doubleValue()}
   */
  @Override
  public double getBigDecimalDouble(String columnName) {
    String v = getString(columnName);

    return new BigDecimal(v).doubleValue();
  }

  /**
   * Take the value of that column`s value as a BigDecimal by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a big decimal.
   */
  @Override
  public BigDecimal getBigDecimal(String columnName) {
    String v = getString(columnName);

    return new BigDecimal(v);
  }

  /**
   * Checking database null equals the value of that column`s value by the column`s name.
   *
   * @param columnName column`s name
   */
  @Override
  public boolean isNull(String columnName) {
    return data.get(columnName) == null;
  }

  /** Clear all value of a single row. */
  public void clear() {
    data.clear();
  }
}
