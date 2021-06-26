package ra.db.connection;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Get statement.
 *
 * @author Ray Li
 */
public interface OnCreatedStatementListener {
  void onCreatedStatement(Statement st) throws SQLException;
}
