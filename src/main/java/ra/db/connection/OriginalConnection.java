package ra.db.connection;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import ra.db.DatabaseConnection;
import ra.db.DatabaseKeepAlive;
import ra.db.StatementExecutor;
import ra.db.parameter.DatabaseParameters;

/**
 * 執行任何SQL語法皆無lock，執行 {@link #insert(String)}時，回傳的結果有機率是錯誤的.
 *
 * @author Ray Li
 */
public class OriginalConnection implements DatabaseConnection {
  private boolean startThread = false;
  private StatementExecutor executor;
  private DatabaseParameters param;
  private Connection connection = null;
  private volatile boolean volatileIsLive = false;

  private DatabaseKeepAlive isLive;

  /**
   * .
   *
   * @param param 資料庫連線相關參數
   */
  public OriginalConnection(DatabaseParameters param) {
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
    return consumer.applay(connection);
  }

  /**
   * 連線到 DB.
   *
   * @return 連線成功回傳1，失敗回傳0
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
      volatileIsLive = false;
    }
    return false;
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
