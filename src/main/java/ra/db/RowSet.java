package ra.db;

import java.math.BigDecimal;

/**
 * Query row.
 *
 * @author Ray Li
 */
public interface RowSet {
  /**
   * 以指定欄位名稱取得原始byte格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a byte array.
   */
  public byte[] getBlob(String columnName);

  /**
   * 以指定欄位名稱取得原始String格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a String.
   */
  public String getString(String columnName);

  /**
   * 以指定欄位名稱取得原始String格式資料.
   *
   * @param columnName 欄位名稱
   * @param charsetName Charset
   * @return the value of that column as a String.
   */
  public String getString(String columnName, String charsetName);

  /**
   * 以指定欄位名稱取得原始short格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a short.
   */
  public short getShort(String columnName);

  /**
   * 以指定欄位名稱取得原始int格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as an int.
   */
  public int getInt(String columnName);

  /**
   * 以指定欄位名稱取得原始long格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a long.
   */
  public long getLong(String columnName);

  /**
   * 以指定欄位名稱取得原始float格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a float.
   */
  public float getFloat(String columnName);

  /**
   * 以指定欄位名稱取得原始double格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a double.
   */
  public double getDouble(String columnName);

  /**
   * 以指定欄位名稱取得BigDecimal轉換double的格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a {@link BigDecimal#doubleValue()}
   */
  public double getDoubleDecima(String columnName);

  /**
   * 以指定欄位名稱取得原始BigDecimal格式資料.
   *
   * @param columnName 欄位名稱
   * @return the value of that column as a big decimal.
   */
  public BigDecimal getBigDecimal(String columnName);

  /**
   * 以指定欄位名稱檢查欄位值為資料庫NULL.
   *
   * @param columnName 欄位名稱
   */
  public boolean isNull(String columnName);
}
