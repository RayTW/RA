package ra.db.connection;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import ra.db.DatabaseConnection;
import ra.db.DatabaseKeepAlive;
import ra.db.StatementExecutor;
import ra.db.parameter.DatabaseParameters;

/**
 * Provide execute SQL statement using a keep database connection, and the connection is thread
 * safe.
 *
 * @author Ray Li
 */
public class ConcurrentConnection implements DatabaseConnection {
  private boolean startThread = false;
  private StatementExecutor executor;
  private volatile boolean volatileIsLive = false;
  private DatabaseParameters param;
  private Connection connection = null;
  private DatabaseKeepAlive isLive;

  private Object lock = new Object();

  /**
   * Initialize.
   *
   * @param param database connection settings.
   */
  public ConcurrentConnection(DatabaseParameters param) {
    this.param = param;
    loadDriveInstance(param);
    executor = createStatementExecutor();
    isLive = new DatabaseKeepAlive(this);
    isLive.start();
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
    synchronized (lock) {
      return consumer.applay(connection);
    }
  }

  /**
   * Connect to database.
   *
   * @return If connect successful return true.
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
      volatileIsLive = true;
      startThread = true;
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      volatileIsLive = false;
      return false;
    }
  }

  @Override
  public boolean isLive() {
    return volatileIsLive;
  }

  @Override
  public void keep() {
    if (startThread) {
      try {
        executor.executeQuery("SELECT 1", e -> reconnect());
      } catch (Exception e) {
        reconnect();
      }
    }
  }

  private void reconnect() {
    volatileIsLive = false;
    connect();
  }

  @Override
  public void close() throws SQLException {
    isLive.close();
    volatileIsLive = false;
    startThread = false;
    getConnection().close();
  }
}
