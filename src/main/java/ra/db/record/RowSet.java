package ra.db.record;

import java.math.BigDecimal;
import java.util.List;

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
  public byte[] getBytes(String columnName);

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
   * @return the value of that column as a big decimal.
   */
  public BigDecimal getBigDecimal(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param <T> list element
   * @param columnName column name
   * @param castClass class
   * @return the value of that column as a array.
   */
  public <T> List<T> getArray(String columnName, Class<T[]> castClass);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return the value of that column as a object.
   */
  public Object getObject(String columnName);

  /**
   * Gets value uses the specific column name.
   *
   * @param columnName column name
   * @return if value are null returns true.
   */
  public boolean isNull(String columnName);
}
