package ra.db;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import ra.db.parameter.DatabaseParameters;

/**
 * Database Connection.
 *
 * @author Ray Li
 */
public interface DatabaseConnection extends KeepAlive, AutoCloseable {

  @Override
  public default long interval() {
    return 5000;
  }

  /**
   * Load driver.
   *
   * @param param The connection database parameters.
   */
  public default void loadDriveInstance(DatabaseParameters param) {
    try {
      Class.forName(param.getDriver()).newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create StatementExecutor.
   *
   * @return StatementExecutor
   */
  public default StatementExecutor createStatementExecutor() {
    return new StatementExecutor(this);
  }

  /**
   * Try get a database connection from database pool.
   *
   * @param param The connection database parameters.
   */
  public default Connection tryGetConnection(DatabaseParameters param)
      throws SQLException, ConnectException {
    Connection connection = getConnection();

    if (connection != null) {
      connection.close();
      connection = null;
    }
    String dbconn = param.getDatabaseUrl();
    System.out.println("DBConn:" + dbconn);
    Connection connectionTemp = null;

    if (param.getUser() != null && param.getPassword() != null) {
      connectionTemp = DriverManager.getConnection(dbconn, param.getUser(), param.getPassword());
    } else {
      connectionTemp = DriverManager.getConnection(dbconn);
    }

    param.setupConnection(connectionTemp);
    dbconn = null;

    return connectionTemp;
  }

  /**
   * Try to execute SQL (INSERT,UPDATE,DELETE) statements.
   *
   * @param sql statements
   * @param listener exception
   * @return Execute success count.
   */
  public default int tryExecute(String sql, Consumer<Exception> listener) {
    int ret = 0;
    Connection connection = getConnection();

    if (connection == null) {
      String msg = "[與DB主機無法連線]" + getParam();
      System.out.println(msg);

      if (listener != null) {
        listener.accept(new ConnectException(msg));
      }
      return ret;
    }
    try {
      connection.setAutoCommit(false);

      try (Statement st = connection.createStatement()) {
        ret = st.executeUpdate(sql);
      }

      connection.rollback();
    } catch (Exception e) {
      e.printStackTrace();
      if (listener != null) {
        listener.accept(e);
      }
    }

    return ret;
  }

  /**
   * If Database connection connected return {@link StatementExecutor}.
   *
   * @return If connected return true.
   */
  public default boolean connectIf(Consumer<StatementExecutor> executor) {
    if (connect()) {
      executor.accept(new StatementExecutor(this));
      return true;
    }
    return false;
  }

  /**
   * Get the connection state.
   *
   * @return Connection state.
   */
  public abstract boolean connect();

  public abstract DatabaseParameters getParam();

  /**
   * Get the current database connection.
   *
   * @return Database original connection.
   */
  public abstract Connection getConnection();

  public abstract int getConnection(ConnectionFunction consumer)
      throws SQLException, ConnectException;

  public abstract boolean isLive();

  /** Get the current database connection. */
  public static interface ConnectionFunction {
    public abstract int applay(Connection connection) throws SQLException, ConnectException;
  }
}
