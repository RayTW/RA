package test.mock.resultset;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.function.Consumer;
import ra.db.MockResultSet;

/** Test class. */
public class MockCustomTableResultSet extends MockResultSet {
  private String[] columnLabel;
  private Table table;
  private int cursor;

  /** 初炲化各種資料型別欄位名稱 . */
  public MockCustomTableResultSet(Consumer<Table> columnsValue, String... columnLabel) {
    String[] target = new String[columnLabel.length + 1];
    System.arraycopy(columnLabel, 0, target, 1, target.length - 1);
    this.columnLabel = target;
    table = new Table(columnLabel);
    columnsValue.accept(table);
    cursor = 0;
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
    if (cursor + 1 > table.size()) {
      return false;
    }
    cursor++;
    return true;
  }

  /**
   * 取得指定索引欄位的資料byte.
   *
   * @param column 索引
   */
  @Override
  public byte[] getBytes(int column) {
    return table.getBytes(column - 1, cursor - 1);
  }
}
