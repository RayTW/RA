package ra.db.parameter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Set default connection settings after connecting to a database.
 *
 * @author Ray Li
 */
public interface ConnectionSetupable {
  /**
   * Take the connection object, after the connection object has connected. Then setting it.
   *
   * @param connection db`s connect object
   * @throws SQLException SQL error response from database
   */
  public void setupConnection(Connection connection) throws SQLException;
}
