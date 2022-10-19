package ra.db.record;

import java.math.BigDecimal;
import java.util.List;

/**
 * Row of query result.
 *
 * @author Ray Li
 */
public class Row implements RowSet {
  private Record record;

  /** Initialize. */
  public Row(Record record) {
    this.record = record;
  }

  /**
   * Take the value of that column`s value as a byte array by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a byte array.
   */
  @Override
  public byte[] getBytes(String columnName) {
    return record.fieldBytes(columnName);
  }

  /**
   * Take the value of that column`s value as a String by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a String.
   */
  @Override
  public String getString(String columnName) {
    return record.field(columnName);
  }

  /**
   * Take the value of that column`s value as a int by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as an int.
   */
  @Override
  public int getInt(String columnName) {
    return record.fieldInt(columnName);
  }

  /**
   * Take the value of that column`s value as a long by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a long.
   */
  @Override
  public long getLong(String columnName) {
    return record.fieldLong(columnName);
  }

  /**
   * Take the value of that column`s value as a float by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a float.
   */
  @Override
  public float getFloat(String columnName) {
    return record.fieldFloat(columnName);
  }

  /**
   * Take the value of that column`s value as a double by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a double.
   */
  @Override
  public double getDouble(String columnName) {
    return record.fieldDouble(columnName);
  }

  /**
   * Take the value of that column`s value as a BigDecimal by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a big decimal.
   */
  @Override
  public BigDecimal getBigDecimal(String columnName) {
    return record.fieldBigDecimal(columnName);
  }

  /**
   * Take the value of that column`s value as a BigDecimal by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a array.
   */
  @Override
  public <T> List<T> getArray(String columnName, Class<T[]> castClass) {
    return record.fieldArray(columnName, castClass);
  }

  /**
   * Take the value of that column`s value as a Object by the column`s name.
   *
   * @param columnName column`s name
   * @return the value of that column as a big decimal.
   */
  @Override
  public Object getObject(String columnName) {
    return record.fieldObject(columnName);
  }

  /**
   * Checking database null equals the value of that column`s value by the column`s name.
   *
   * @param columnName column`s name
   */
  @Override
  public boolean isNull(String columnName) {
    return record.isNull(columnName);
  }
}
