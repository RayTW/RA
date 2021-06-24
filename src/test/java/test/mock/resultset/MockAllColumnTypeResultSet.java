package test.mock.resultset;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import ra.db.MockResultSet;

/** Test class. */
public class MockAllColumnTypeResultSet extends MockResultSet {
  private boolean hasNext = true;
  private String[] columnLabel;
  private CopyOnWriteArrayList<byte[]> columnsValue;

  /** 初炲化各種資料型別欄位名稱 . */
  public MockAllColumnTypeResultSet() {
    columnLabel =
        new String[] {
          null,
          "Blob",
          "String",
          "Short",
          "Int",
          "Long",
          "Float",
          "Double",
          "DoubleDecima",
          "BigDecimal"
        };
    columnsValue = new CopyOnWriteArrayList<>();

    columnsValue.add(null);
    columnsValue.add("blobValue".getBytes());
    columnsValue.add("StringValue".getBytes());
    columnsValue.add(String.valueOf(Short.MAX_VALUE).getBytes());
    columnsValue.add(String.valueOf(Integer.MAX_VALUE).getBytes());
    columnsValue.add(String.valueOf(Long.MAX_VALUE).getBytes());
    columnsValue.add(String.valueOf(Float.MAX_VALUE).getBytes());
    columnsValue.add(String.valueOf(Double.MAX_VALUE).getBytes());
    columnsValue.add(String.valueOf(new BigDecimal("0.33333333333")).getBytes());
    columnsValue.add(String.valueOf(new BigDecimal(String.valueOf(Math.PI))).getBytes());
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return new MockResultSetMetaData() {
      @Override
      public int getColumnCount() throws SQLException {
        return columnLabel.length - 1;
      }

      @Override
      public String getColumnLabel(int column) throws SQLException {
        return columnLabel[column];
      }
    };
  }

  @Override
  public boolean next() {
    return hasNext;
  }

  /**
   * 取得指定索引欄位的資料byte.
   *
   * @param column 索引
   */
  @Override
  public byte[] getBytes(int column) {
    if (hasNext) {
      hasNext = false;
    }

    return columnsValue.get(column);
  }
}
