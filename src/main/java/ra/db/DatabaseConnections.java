package ra.db;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.StampedLock;
import ra.db.connection.ConcurrentConnection;
import ra.db.connection.OriginalConnection;
import ra.db.parameter.DatabaseParameters;

/**
 * Provider multi connections to a single database, which connections are kept connected. Take
 * connection is round-robin.
 *
 * @author Ray Li
 */
public class DatabaseConnections {
  private String name;
  private List<DatabaseConnectionHolder> connectionPool;
  private int index = 0;
  private StampedLock lock;

  public DatabaseConnections() {
    this(null);
  }

  /**
   * Create database connection.
   *
   * @param name Connection alias.
   */
  public DatabaseConnections(String name) {
    this.name = name;
    connectionPool = new ArrayList<DatabaseConnectionHolder>();
    lock = new StampedLock();
  }

  /**
   * Returns connection alias of database.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Create the Connect Object are kept connected. Connect Object are Thread-safe.
   *
   * @param param The parameters of database connect setting.
   * @param count Kept connections count by the same database.
   */
  public void connectConcurrentConnection(DatabaseParameters param, int count) {
    try {
      connect(param, count, () -> new ConcurrentConnection(param));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * There is non lock when executes SQL, probable return error results when execute insert.
   *
   * @param param The parameters of database connect setting.
   * @param count Kept connections count by the same database.
   */
  public void connectOriginalConnection(DatabaseParameters param, int count) {
    try {
      connect(param, count, () -> new OriginalConnection(param));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create Kept connections.
   *
   * @param param The parameters of database connect setting.
   * @param count Kept connections count by the same database.
   * @param connectionMode Need to be used database connect Object.
   * @throws Exception Throwable any exception, Throw the ConnectException when connect fail.
   */
  public void connect(
      DatabaseParameters param, int count, Callable<DatabaseConnection> connectionMode)
      throws Exception {
    Objects.requireNonNull(connectionMode, "connectionMode == null, connectionMode is required");

    if (name == null) {
      name = param.getDatabaseUrl();
    }

    for (int i = 0; i < count; i++) {
      DatabaseConnection db = connectionMode.call();

      if (!db.connectIf(
          executor -> connectionPool.add(new DatabaseConnectionHolder(db, executor)))) {
        throw new ConnectException("Database name[" + name + "],index[" + i + "] connect failure!");
      }
    }
  }

  /**
   * Take StatementExecutor by round-robin, It's practical when had multi connections.
   *
   * @return {@link StatementExecutor}
   */
  public StatementExecutor next() {
    int index = 0;
    long stamp = lock.writeLock();

    try {
      index = this.index;
      index++;
      if (index >= connectionPool.size()) {
        index = 0;
      }
    } finally {
      lock.unlockWrite(stamp);
    }

    return connectionPool.get(index).statementExecutor;
  }

  public StatementExecutor getStatementExecutor(int index) {
    return connectionPool.get(index).statementExecutor;
  }

  public DatabaseConnection getConnection(int index) {
    return connectionPool.get(index).dbConnection;
  }

  /** Close all database connection. */
  public void close() {
    for (DatabaseConnectionHolder db : connectionPool) {
      try {
        db.dbConnection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    connectionPool.clear();
  }

  private static class DatabaseConnectionHolder {
    private StatementExecutor statementExecutor;
    private DatabaseConnection dbConnection;

    DatabaseConnectionHolder(DatabaseConnection db, StatementExecutor set) {
      statementExecutor = set;
      dbConnection = db;
    }
  }
}
