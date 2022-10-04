package ra.db;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import ra.db.record.Record;
import ra.db.record.RecordCursor;
import ra.db.record.RecordSet;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;

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
  public int execute(String sql) throws RaConnectException, RaSqlException {
    int ret = 0;
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      throw new RaConnectException(msg);
    }

    try {
      ret = executeSql(sql);
    } catch (Exception e) {
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
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
  public int tryExecute(String sql) throws RaConnectException, RaSqlException {
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      throw new RaConnectException(msg);
    }

    try {
      return connection.tryExecute(sql);
    } catch (ConnectException e) {
      throw new RaConnectException(e);
    } catch (SQLException e) {
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
    }
  }

  /**
   * A SQL statement is precompiled and stored in a Prepared object. This object can then be used to
   * efficiently execute this statement multiple times.
   *
   * @param prepared prepared
   * @return RecordCursor
   * @throws ConnectException ConnectException
   * @throws SQLException SQLException
   */
  @Override
  public RecordCursor executeQueryUsePrepare(Prepared prepared)
      throws RaConnectException, RaSqlException {
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + prepared.getSql();

      throw new RaConnectException(msg);
    }
    Record record = buildRecord();

    try {
      connection.getConnection(
          dbConnection -> {
            dbConnection.setAutoCommit(true);

            try (PreparedStatement st = dbConnection.prepareStatement(prepared.getSql())) {
              for (Entry<Integer, ParameterValue> element : prepared.getValues().entrySet()) {
                ParameterValue paramter = element.getValue();

                if (Boolean.class.isAssignableFrom(paramter.getType())) {
                  st.setBoolean(element.getKey(), Boolean.class.cast(paramter.getValue()));
                } else if (String.class.isAssignableFrom(paramter.getType())) {
                  st.setString(element.getKey(), String.class.cast(paramter.getValue()));
                } else if (Integer.class.isAssignableFrom(paramter.getType())) {
                  st.setInt(element.getKey(), Integer.class.cast(paramter.getValue()));
                } else if (Long.class.isAssignableFrom(paramter.getType())) {
                  st.setLong(element.getKey(), Long.class.cast(paramter.getValue()));
                } else if (Double.class.isAssignableFrom(paramter.getType())) {
                  st.setDouble(element.getKey(), Double.class.cast(paramter.getValue()));
                } else if (Float.class.isAssignableFrom(paramter.getType())) {
                  st.setFloat(element.getKey(), Float.class.cast(paramter.getValue()));
                } else if (BigDecimal.class.isAssignableFrom(paramter.getType())) {
                  st.setBigDecimal(element.getKey(), BigDecimal.class.cast(paramter.getValue()));
                } else if (byte[].class.isAssignableFrom(paramter.getType())) {
                  st.setBytes(element.getKey(), byte[].class.cast(paramter.getValue()));
                } else if (Blob.class.isAssignableFrom(paramter.getType())) {
                  st.setBlob(element.getKey(), Blob.class.cast(paramter.getValue()));
                } else {
                  throw new IllegalArgumentException(
                      "Unsupported object type for QueryParameter: " + paramter.getType());
                }
              }
              try (ResultSet rs = st.executeQuery()) {
                record.convert(rs);
              }
            }
            return 0;
          });
    } catch (Exception e) {
      throw new RaSqlException(
          "SQL Syntax Error, sql=" + prepared.getSql() + ",values = " + prepared.getValues(), e);
    }

    return record;
  }

  /**
   * Transaction.
   *
   * @throws SQLException SQLException
   * @throws ConnectException ConnectException
   */
  @Override
  public void executeTransaction(TransactionExecutor executor)
      throws RaConnectException, RaSqlException {
    if (connection == null) {
      throw new RaConnectException("Connect to database failed.");
    }

    try {
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
    } catch (ConnectException e) {
      throw new RaConnectException(e);
    } catch (SQLException e) {
      throw new RaSqlException(e);
    }
  }

  /**
   * Execute SQL statements.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  @Override
  public int executeCommit(List<String> sql) throws RaConnectException, RaSqlException {

    int ret = 0;

    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      throw new RaConnectException(msg);
    }

    try {
      ret = executeCommit(false, sql);
    } catch (Exception e) {
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
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
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      throw new RaConnectException(msg);
    }
    int ret = 0;
    try {
      ret = lastInsertId(sql);
    } catch (Exception e) {
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
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
  public RecordCursor executeQuery(String sql) throws RaConnectException, RaSqlException {
    if (!isLive()) {
      String msg =
          "Connect to database failed, param :"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      throw new RaConnectException(msg);
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
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
    }

    return record;
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
