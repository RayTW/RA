package ra.db.connection;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * 取得Statement.
 *
 * @author Ray Li
 */
public interface OnCreatedStatementListener {
  void onCreatedStatement(Statement st) throws SQLException;
}
