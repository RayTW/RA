package ra.db;

import java.math.BigDecimal;

/**
 * Query row.
 *
 * @author Ray Li
 */
public interface RowSet {
  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a byte array.
   */
  public byte[] getBlob(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a String.
   */
  public String getString(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @param charsetName Charset
   * @return the value of that column as a String.
   */
  public String getString(String columnName, String charsetName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a short.
   */
  public short getShort(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as an int.
   */
  public int getInt(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a long.
   */
  public long getLong(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a float.
   */
  public float getFloat(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a double.
   */
  public double getDouble(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a {@link BigDecimal#doubleValue()}
   */
  public double getDoubleDecima(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a big decimal.
   */
  public BigDecimal getBigDecimal(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return if value are null returns true.
   */
  public boolean isNull(String columnName);
}
