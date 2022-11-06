package ra.db.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Record.
 *
 * @author Ray Li
 */
public interface Record extends RecordCursor {
  /**
   * Returns name of column.
   *
   * @param index index
   * @return column name
   */
  public String getColumnName(int index);

  /**
   * Convert the result of a query.
   *
   * @param result Result of query.
   * @throws SQLException SQLException
   */
  public void convert(ResultSet result) throws SQLException;

  /**
   * Returns last insert id.
   *
   * @param statement statement
   * @return id auto-increment id
   * @throws SQLException SQLException
   */
  public LastInsertId getLastInsertId(Statement statement) throws SQLException;
}
