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
  private List<DatabaseConnectionHolder> dbHolders;
  private int index = 0;
  private StampedLock lock;

  public DatabaseConnections() {
    this(null);
  }

  /**
   * Create Database Connect Object.
   *
   * @param name Connect Object alias.
   */
  public DatabaseConnections(String name) {
    this.name = name;
    dbHolders = new ArrayList<DatabaseConnectionHolder>();
    lock = new StampedLock();
  }

  /** Take Connect Object alias. */
  public String getName() {
    return name;
  }

  /**
   * Create the Connect Object are kept connected. Connect Object are Thread-safe.
   *
   * @param param Parameters of Database connect setting.
   * @param count Kept connections count by the same database.
   * @throws ConnectException Throw the ConnectException when connect fail.
   */
  public void connectConcurrentConnection(DatabaseParameters param, int count) {
    try {
      connect(param, count, () -> new ConcurrentConnection(param));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * There is non lock when executes SQL, probable return error results when execute {@link
   * #insert(String)}.
   *
   * @param param Parameters of Database connect setting.
   * @param count Kept connections count by the same database.
   * @throws ConnectException Throw the ConnectException when connect fail.
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
   * @param param Parameters of Database connect setting.
   * @param count Kept connections count by the same database.
   * @param newDbObj Need to be Used Database Connect Object.
   * @throws Exception Throwable Any Exception, Throw the ConnectException when connect fail.
   */
  public void connect(DatabaseParameters param, int count, Callable<DatabaseConnection> newDbObj)
      throws Exception {
    Objects.requireNonNull(newDbObj, "newDBObj == null, newDBObj is required");

    if (name == null) {
      name = param.getDatabaseUrl();
    }

    for (int i = 0; i < count; i++) {
      DatabaseConnection db = newDbObj.call();

      if (db.connectIf(executor -> dbHolders.add(new DatabaseConnectionHolder(db, executor)))) {
        System.out.println("資料庫連線[" + name + "],index[" + i + "]連線成功 !");
      } else {
        throw new ConnectException("資料庫連線[" + name + "],index[" + i + "]連線失敗!");
      }
    }
  }

  /** Take StatementExecutor by round-robin, It's practical when had multi connections. */
  public StatementExecutor next() {
    int index = 0;
    long stamp = lock.writeLock();

    try {
      index = this.index;
      index++;
      if (index >= dbHolders.size()) {
        index = 0;
      }
    } finally {
      lock.unlockWrite(stamp);
    }

    return dbHolders.get(index).statementExecutor;
  }

  public StatementExecutor getStatementExecutor(int index) {
    return dbHolders.get(index).statementExecutor;
  }

  public DatabaseConnection getDbConnection(int index) {
    return dbHolders.get(index).dbConnection;
  }

  /** Close all the Database connection. */
  public void close() {
    for (DatabaseConnectionHolder db : dbHolders) {
      try {
        db.dbConnection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    dbHolders.clear();
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
