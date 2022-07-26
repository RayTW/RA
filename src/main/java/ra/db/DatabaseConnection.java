package ra.db;

import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import ra.db.parameter.ConnectionSetupable;
import ra.db.parameter.DatabaseParameters;

/**
 * Database Connection.
 *
 * @author Ray Li
 */
public interface DatabaseConnection extends AutoCloseable {

  /**
   * Load driver.
   *
   * @param param The parameters of database connect setting.
   */
  public default void loadDriveInstance(DatabaseParameters param) {
    try {
      Class.forName(param.getDriver()).getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException
        | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create StatementExecutor.
   *
   * @return StatementExecutor
   */
  public default StatementExecutor createStatementExecutor() {
    return new JdbcExecutor(this);
  }

  /**
   * Try to get a database connection from database pool.
   *
   * @param param The parameters of database connect setting.
   * @return {@link Connection}
   * @throws SQLException SQLException
   * @throws ConnectException ConnectException
   */
  public default Connection tryGetConnection(DatabaseParameters param)
      throws SQLException, ConnectException {
    Connection connection = getConnection();

    if (connection != null) {
      connection.close();
      connection = null;
    }
    String dbconn = param.getDatabaseUrl();
    Connection connectionTemp = null;

    if (param.getUser() != null && param.getPassword() != null) {
      connectionTemp = DriverManager.getConnection(dbconn, param.getUser(), param.getPassword());
    } else {
      connectionTemp = DriverManager.getConnection(dbconn);
    }

    if (param instanceof ConnectionSetupable) {
      ConnectionSetupable setupable = (ConnectionSetupable) param;
      setupable.setupConnection(connectionTemp);
    }

    dbconn = null;

    return connectionTemp;
  }

  /**
   * Try to execute SQL (INSERT,UPDATE,DELETE) statements.
   *
   * @param sql Statements
   * @param listener Exception
   * @return Execute success count.
   */
  public default int tryExecute(String sql, Consumer<Exception> listener) {
    int ret = 0;
    Connection connection = getConnection();

    if (connection == null) {
      if (listener != null) {
        listener.accept(new ConnectException("Connect to database failed." + getParam()));
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
   * If database connection connected return {@link StatementExecutor}.
   *
   * @param executor Executor
   * @return If connected return TRUE.
   */
  public default boolean connectIf(Consumer<StatementExecutor> executor) {
    if (connect()) {
      executor.accept(new JdbcExecutor(this));
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

  /**
   * Returns database properties settings.
   *
   * @return DatabaseParameters
   */
  public abstract DatabaseParameters getParam();

  /**
   * Get current database connection.
   *
   * @return Database original connection.
   */
  public abstract Connection getConnection();

  /**
   * Get current database connection.
   *
   * @param consumer consumer
   * @return Database original connection.
   * @throws SQLException SQLException
   * @throws ConnectException if can't to connect database.
   */
  public abstract int getConnection(ConnectionFunction consumer)
      throws SQLException, ConnectException;

  /**
   * Returns database state whether available.
   *
   * @return database state
   */
  public abstract boolean isLive();

  /** Get current database connection. */
  public static interface ConnectionFunction {
    /**
     * Get current database connection.
     *
     * @param connection connection
     * @return result
     * @throws SQLException SQLException
     * @throws ConnectException if can't to connect database.
     */
    public abstract int applay(Connection connection) throws SQLException, ConnectException;
  }
}
