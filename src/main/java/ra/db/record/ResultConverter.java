package ra.db.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Convert the result of a database query.
 *
 * @author Ray Li
 */
public interface ResultConverter {
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
  public int getLastInsertId(Statement statement) throws SQLException;
}
