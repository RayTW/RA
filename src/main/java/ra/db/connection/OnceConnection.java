package ra.db.connection;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import ra.db.DatabaseConnection;
import ra.db.parameter.DatabaseParameters;

/**
 * Provide execute SQL statement using a database connection, and the connection is not thread safe.
 * The connection is not keeping alive.
 *
 * @author Ray Li
 */
public class OnceConnection implements DatabaseConnection {
  private DatabaseParameters param;
  private Connection connection = null;
  private volatile boolean isLive = false;

  /**
   * Initialize.
   *
   * @param param database settings.
   */
  public OnceConnection(DatabaseParameters param) {
    this.param = param;
    loadDriveInstance(param);
  }

  @Override
  public DatabaseParameters getParam() {
    return param;
  }

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public int getConnection(ConnectionFunction consumer) throws SQLException, ConnectException {
    return consumer.applay(getConnection());
  }

  /**
   * Get the connection state.
   *
   * @return Connection state.
   */
  @Override
  public boolean connect() {
    try {
      DatabaseParameters param = getParam();
      Connection connectionTemp = null;

      if (connection == null) {
        connectionTemp = tryGetConnection(param);
        connection = connectionTemp;
      } else {
        synchronized (connection) {
          connectionTemp = tryGetConnection(param);
          connection = connectionTemp;
        }
      }
      isLive = true;
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      isLive = false;

      return false;
    }
  }

  @Override
  public boolean isLive() {
    return isLive;
  }

  @Override
  public void close() {
    isLive = false;
    try {
      getConnection().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
