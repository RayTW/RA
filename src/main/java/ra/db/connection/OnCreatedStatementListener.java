package ra.db.connection;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Get statement.
 *
 * @author Ray Li
 */
public interface OnCreatedStatementListener {
  /**
   * Register listener that receives create SQL statements.
   *
   * @param st SQL statement
   * @throws SQLException SQLException
   */
  void onCreatedStatement(Statement st) throws SQLException;
}
