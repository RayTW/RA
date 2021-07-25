package ra.db;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;
import ra.db.record.Record;
import ra.db.record.RecordCursor;

/**
 * Like as transaction query.
 *
 * @author Ray Li
 */
public class MultiQuery implements Closeable {
  private Statement statement = null;
  private Supplier<Record> builder = null;

  public MultiQuery(Supplier<Record> recordBuilder, Statement st) {
    builder = recordBuilder;
    statement = st;
  }

  /**
   * Execute query statement.
   *
   * @param sql SQL statement
   * @return RecordCursor query result
   * @throws SQLException sql error
   */
  public RecordCursor executeQuery(String sql) throws SQLException {
    Record recordSet = builder.get();

    try (ResultSet rs = statement.executeQuery(sql); ) {
      recordSet.convert(rs);
    }
    return recordSet;
  }

  @Override
  public void close() {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
