package ra.db;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import ra.db.parameter.Accountable;
import ra.db.parameter.DatabaseParameters;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;

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
   * @throws RaSqlException RaSqlException
   * @throws RaConnectException RaConnectException
   */
  public default Connection tryGetConnection(DatabaseParameters param)
      throws RaSqlException, RaConnectException {
    Connection connection = getConnection();

    try {
      if (connection != null) {
        connection.close();
        connection = null;
      }
      String dbconn = param.getDatabaseUrl();

      if (param instanceof Accountable) {
        Accountable account = (Accountable) param;

        if (account.getUser() != null && account.getPassword() != null) {
          return DriverManager.getConnection(dbconn, account.getUser(), account.getPassword());
        }
      }

      return DriverManager.getConnection(dbconn);
    } catch (SQLException e) {
      throw new RaSqlException("Attempt to acquire connection failed. " + param, e);
    }
  }

  /**
   * Try to execute SQL (INSERT,UPDATE,DELETE) statements.
   *
   * @param sql Statements
   * @return Execute success count.
   */
  public default int tryExecute(String sql) throws RaSqlException, RaConnectException {
    int ret = 0;
    try {
      Connection connection = getConnection();

      if (connection == null) {
        throw new RaConnectException("Connect to database failed." + getParam());
      }
      connection.setAutoCommit(false);

      try (Statement st = connection.createStatement()) {
        ret = st.executeUpdate(sql);
      }

      connection.rollback();
    } catch (SQLException e) {
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
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
   * @throws RaSqlException RaSqlException
   * @throws RaConnectException if can't to connect database.
   */
  public abstract int getConnection(ConnectionFunction consumer)
      throws RaSqlException, RaConnectException;

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
     * @throws RaSqlException RaSqlException
     * @throws RaConnectException if can't to connect database.
     */
    public abstract int applay(Connection connection) throws RaSqlException, RaConnectException;
  }
}
