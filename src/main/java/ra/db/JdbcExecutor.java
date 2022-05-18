package ra.db;

import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import ra.db.connection.OnCreatedStatementListener;
import ra.db.record.Record;
import ra.db.record.RecordCursor;
import ra.db.record.RecordSet;

/**
 * SQL statement (CRUD) executor.
 *
 * @author Ray Li
 */
public class JdbcExecutor implements StatementExecutor {
  private DatabaseConnection connection;

  /**
   * Initialize.
   *
   * @param db database connection.
   */
  public JdbcExecutor(DatabaseConnection db) {
    connection = db;
  }

  @Override
  public boolean isLive() {
    return connection.isLive();
  }

  /**
   * If the execution is successful, the return count.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  @Override
  public int execute(String sql) {
    int ret = 0;
    if (!isLive()) {
      return ret;
    }

    return execute(sql, e -> e.printStackTrace());
  }

  /**
   * If the execution is successful, the return count.
   *
   * @param sql SQL statements
   * @param listener exception
   * @return affected rows
   */
  @Override
  public int execute(String sql, Consumer<Exception> listener) {
    int ret = 0;
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      if (listener != null) {
        listener.accept(new ConnectException(msg));
      }
      return ret;
    }

    try {
      ret = executeSql(sql);
    } catch (Exception e) {
      if (listener != null) {
        listener.accept(e);
      }
    }
    return ret;
  }

  private int executeSql(String sql) throws ConnectException, SQLException {
    int ret =
        this.connection.getConnection(
            dbConnection -> {
              dbConnection.setAutoCommit(true);
              try (Statement st = dbConnection.createStatement()) {
                return st.executeUpdate(sql);
              }
            });

    return ret;
  }

  /**
   * Try to execute SQL statement (CRUD).
   *
   * @param sql SQL statement
   * @return affected rows
   */
  @Override
  public int tryExecute(String sql) {
    return tryExecute(sql, null);
  }

  /**
   * Try to execute SQL statement (CRUD).
   *
   * @param sql SQL statement
   * @param listener exception
   * @return affected rows
   */
  @Override
  public int tryExecute(String sql, Consumer<Exception> listener) {
    int ret = 0;

    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      if (listener != null) {
        listener.accept(new ConnectException(msg));
      }
      return ret;
    }

    return connection.tryExecute(sql, listener);
  }

  /**
   * transaction.
   *
   * @throws SQLException SQLException
   * @throws ConnectException ConnectException
   */
  @Override
  public void executeTransaction(TransactionExecutor executor)
      throws ConnectException, SQLException {
    if (connection == null) {
      throw new ConnectException("Connect to database failed.");
    }

    this.connection.getConnection(
        dbConnection -> {
          boolean ret = false;

          try {
            dbConnection.setAutoCommit(false);
            try (Statement st = dbConnection.createStatement()) {
              Transaction tran = new Transaction(st);
              ret = executor.apply(tran);
            }
          } finally {
            if (ret) {
              dbConnection.commit();
            } else {
              try {
                dbConnection.rollback();
              } catch (SQLException e) {
                e.printStackTrace();
              }
            }
          }
          return 0;
        });
  }

  /**
   * Execute SQL statements.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  @Override
  public int executeCommit(List<String> sql) {
    int ret = 0;
    if (!isLive()) {
      return ret;
    }

    return executeCommit(sql, e -> e.printStackTrace());
  }

  /**
   * Execute SQL statements.
   *
   * @param sql SQL statement
   * @param listener exception
   * @return affected rows
   */
  @Override
  public int executeCommit(List<String> sql, Consumer<Exception> listener) {
    int ret = 0;

    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      if (listener != null) {
        listener.accept(new ConnectException(msg));
      }
      return ret;
    }

    try {
      ret = executeCommit(false, sql);
    } catch (Exception e) {
      if (listener != null) {
        listener.accept(e);
      }
    }
    return ret;
  }

  private int executeCommit(boolean autoCommit, List<String> sqls)
      throws SQLException, ConnectException {
    return this.connection.getConnection(
        dbConnection -> {
          int ret = 0;

          if (connection == null) {
            throw new ConnectException("Connect to database failed.");
          }
          try {
            dbConnection.setAutoCommit(autoCommit);
            try (Statement st = dbConnection.createStatement()) {
              String sql;
              for (int i = 0; i < sqls.size(); i++) {
                sql = sqls.get(i);
                ret = st.executeUpdate(sql);
              }
            }

            if (!autoCommit) {
              dbConnection.commit();
            }
          } catch (Exception e) {
            e.printStackTrace();
            if (!autoCommit) {
              try {
                dbConnection.rollback();
              } catch (SQLException e1) {
                e1.printStackTrace();
              }
              ret = 0;
            }
            throw e;
          }

          return ret;
        });
  }

  /**
   * Return the last id after executing SQL statement.
   *
   * @param sql SQL statement
   * @return last id
   */
  @Override
  public int insert(String sql) {
    return insert(sql, null);
  }

  /**
   * Return the last id after executing SQL statement.
   *
   * @param sql SQL statement
   * @param errorListener exception
   * @return affected rows
   */
  @Override
  public int insert(String sql, Consumer<Exception> errorListener) {
    int ret = 0;

    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      if (errorListener != null) {
        errorListener.accept(new ConnectException(msg));
      }
      return 0;
    }
    try {
      ret = lastInsertId(sql);
    } catch (Exception e) {
      e.printStackTrace();

      if (errorListener != null) {
        errorListener.accept(e);
      }
    }

    return ret;
  }

  private int lastInsertId(String sql) throws SQLException, ConnectException {
    return this.connection.getConnection(
        dbConnection -> {
          dbConnection.setAutoCommit(true);
          try (Statement st = dbConnection.createStatement()) {
            synchronized (st) {
              if (st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS) > 0) {
                return buildRecord().getLastInsertId(st);
              }
            }
          }
          return 0;
        });
  }

  /**
   * Execute query, ex : SELECT * FROM table.
   *
   * @param sql SQL statement
   * @return RecordCursor
   */
  @Override
  public RecordCursor executeQuery(String sql) {
    return executeQuery(sql, null);
  }

  /**
   * Execute query, ex : SELECT * FROM table.
   *
   * @param sql SQL statement
   * @param exceptionListener SQLException statement invalid、ConnectException connect to database
   *     failed
   * @return RecordCursor
   */
  @Override
  public RecordCursor executeQuery(String sql, Consumer<Exception> exceptionListener) {
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      if (exceptionListener != null) {
        exceptionListener.accept(new ConnectException(msg));
      }

      return buildRecord();
    }
    Record record = buildRecord();
    try {
      connection.getConnection(
          dbConnection -> {
            dbConnection.setAutoCommit(true);

            try (Statement st = dbConnection.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
              record.convert(rs);
            }
            return 0;
          });
    } catch (Exception e) {
      e.printStackTrace();

      if (exceptionListener != null) {
        exceptionListener.accept(e);
      }
    }

    return record;
  }

  /**
   * Execute query( transaction), ex : SELECT * FROM table.
   *
   * @param listener listener
   */
  @Override
  public void multiQuery(Consumer<MultiQuery> listener) {
    if (!isLive()) {
      return;
    }

    try {
      query(
          (st) -> {
            try (MultiQuery multiQuery = new MultiQuery(this::buildRecord, st)) {
              listener.accept(multiQuery);
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Execute query( transaction), ex : SELECT * FROM table.
   *
   * @param listener listener
   * @param exceptionListener exceptionListener
   */
  @Override
  public void multiQuery(Consumer<MultiQuery> listener, Consumer<Exception> exceptionListener) {
    if (!isLive()) {
      return;
    }

    try {
      query(
          (st) -> {
            try (MultiQuery multiQuery = new MultiQuery(this::buildRecord, st)) {
              listener.accept(multiQuery);
            }
          });
    } catch (Exception e) {
      if (exceptionListener != null) {
        exceptionListener.accept(e);
      }
    }
  }

  private void query(OnCreatedStatementListener listener) throws SQLException, ConnectException {
    this.connection.getConnection(
        dbConnection -> {
          dbConnection.setAutoCommit(true);
          try (Statement st = dbConnection.createStatement(); ) {
            listener.onCreatedStatement(st);
          }
          return 0;
        });
  }

  /**
   * Returns instance of inheriting Record.
   *
   * @return Record
   */
  public Record buildRecord() {
    return new RecordSet(this.connection.getParam().getCategory());
  }

  /**
   * Transaction.
   *
   * @author ray_lee
   */
  public class Transaction {
    private Statement statement;

    public Transaction(Statement statement) {
      this.statement = statement;
    }

    /**
     * Execute multiple SQL using a batch.
     *
     * @param sqls sqls
     * @return result
     * @throws SQLException SQLException
     */
    public List<Integer> executeUpdate(List<String> sqls) throws SQLException {
      ArrayList<Integer> rets = new ArrayList<>();
      for (int i = 0; i < sqls.size(); i++) {
        String sql = sqls.get(i);

        int ret = statement.executeUpdate(sql);
        rets.add(ret);
      }
      return rets;
    }

    /**
     * Execute a SQL using a batch.
     *
     * @param sql sql
     * @return execute count
     * @throws SQLException SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
      return statement.executeUpdate(sql);
    }

    /**
     * Execute a SQL using a batch.
     *
     * @param sql sql
     * @return last id
     * @throws SQLException SQLException
     */
    public int insertAndLastId(String sql) throws SQLException {
      if (statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS) > 0) {
        return buildRecord().getLastInsertId(statement);
      }
      return -1;
    }

    /**
     * Execute a query SQL using a batch.
     *
     * @param sql sql
     * @return RecordCursor
     * @throws SQLException SQLException
     */
    public RecordCursor executeQuery(String sql) throws SQLException {
      Record record = buildRecord();
      ResultSet rs = statement.executeQuery(sql);

      record.convert(rs);

      return record;
    }
  }
}
