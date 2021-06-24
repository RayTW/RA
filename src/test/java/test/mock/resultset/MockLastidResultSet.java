package test.mock.resultset;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import ra.db.MockResultSet;

/** Test class. */
public class MockLastidResultSet extends MockResultSet {
  private boolean hasNext = true;
  private long lastid;

  public MockLastidResultSet() {
    lastid = 1;
  }

  public MockLastidResultSet(long lastid) {
    this.lastid = lastid;
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return new MockResultSetMetaData() {
      @Override
      public int getColumnCount() throws SQLException {
        return 1;
      }

      @Override
      public String getColumnLabel(int column) throws SQLException {
        return "lastid";
      }
    };
  }

  @Override
  public boolean next() {
    return hasNext;
  }

  @Override
  public byte[] getBytes(int column) {
    hasNext = false;
    return String.valueOf(lastid).getBytes();
  }
}
