package ra.db;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import ra.db.record.LastInsertId;
import ra.db.record.Record;
import ra.db.record.RecordCursor;
import ra.db.record.RecordSet;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;
import ra.ref.Reference;

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
   * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an SQL
   * statement that returns nothing, such as an SQL DDL statement.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  @Override
  public int executeUpdate(String sql) throws RaConnectException, RaSqlException {
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

    ret =
        this.connection.getConnection(
            dbConnection -> {
              try {
                dbConnection.setAutoCommit(true);
                try (Statement st = dbConnection.createStatement()) {
                  return st.executeUpdate(sql);
                }
              } catch (SQLException e) {
                throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
              }
            });

    return ret;
  }

  /**
   * Attempts to execute the given SQL statement, which may be an INSERT, UPDATE, or DELETE
   * statement or an SQL statement that returns nothing, such as an SQL DDL statement.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  @Override
  public int tryExecuteUpdate(String sql) throws RaConnectException, RaSqlException {
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

    return connection.getConnection(
        dbConnection -> {
          int ret = 0;
          try {
            dbConnection.setAutoCommit(false);

            try (Statement st = dbConnection.createStatement()) {
              ret = st.executeUpdate(sql);
            }

            dbConnection.rollback();
          } catch (SQLException e) {
            throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
          }

          return ret;
        });
  }

  /**
   * Executes the SQL statement in this PreparedStatement object, which must be an SQL Data
   * Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE; or an SQL statement
   * that returns nothing, such as a DDL statement.
   *
   * @param prepared prepared
   * @return RecordCursor
   * @throws RaConnectException RaConnectException
   * @throws RaSqlException RaSqlException
   */
  @Override
  public int prepareExecuteUpdate(Prepared prepared) throws RaConnectException, RaSqlException {
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

    int ret =
        this.connection.getConnection(
            dbConnection -> {
              try {
                dbConnection.setAutoCommit(true);
                try (PreparedStatement st = dbConnection.prepareStatement(prepared.getSql())) {

                  setParametersPreparedStatement(prepared, st);

                  return st.executeUpdate();
                }
              } catch (SQLException e) {
                throw new RaSqlException(
                    "SQL Syntax Error, sql="
                        + prepared.getSql()
                        + ",values="
                        + prepared.getValues(),
                    e);
              }
            });

    return ret;
  }

  /**
   * A SQL statement is precompiled and stored in a Prepared object. This object can then be used to
   * efficiently execute this statement multiple times.
   *
   * @param prepared prepared
   * @return RecordCursor
   * @throws RaConnectException RaConnectException
   * @throws RaSqlException RaSqlException
   */
  @Override
  public RecordCursor prepareExecuteQuery(Prepared prepared)
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

    connection.getConnection(
        dbConnection -> {
          try {
            dbConnection.setAutoCommit(true);

            try (PreparedStatement st = dbConnection.prepareStatement(prepared.getSql())) {

              setParametersPreparedStatement(prepared, st);

              try (ResultSet rs = st.executeQuery()) {
                record.convert(rs);
              }
            }
          } catch (SQLException e) {
            throw new RaSqlException(
                "SQL Syntax Error, sql=" + prepared.getSql() + ",values=" + prepared.getValues(),
                e);
          }
          return 0;
        });

    return record;
  }

  /**
   * Put parameter values ​​into PreparedStatement.
   *
   * @param prepared prepared
   * @param statement statement
   * @throws SQLException SQLException
   */
  private void setParametersPreparedStatement(Prepared prepared, PreparedStatement statement)
      throws SQLException {
    for (Entry<Integer, ParameterValue> element : prepared.getValues().entrySet()) {
      ParameterValue paramter = element.getValue();

      if (Boolean.class.isAssignableFrom(paramter.getType())) {
        statement.setBoolean(element.getKey(), Boolean.class.cast(paramter.getValue()));
      } else if (String.class.isAssignableFrom(paramter.getType())) {
        statement.setString(element.getKey(), String.class.cast(paramter.getValue()));
      } else if (Integer.class.isAssignableFrom(paramter.getType())) {
        statement.setInt(element.getKey(), Integer.class.cast(paramter.getValue()));
      } else if (Long.class.isAssignableFrom(paramter.getType())) {
        statement.setLong(element.getKey(), Long.class.cast(paramter.getValue()));
      } else if (Double.class.isAssignableFrom(paramter.getType())) {
        statement.setDouble(element.getKey(), Double.class.cast(paramter.getValue()));
      } else if (Float.class.isAssignableFrom(paramter.getType())) {
        statement.setFloat(element.getKey(), Float.class.cast(paramter.getValue()));
      } else if (BigDecimal.class.isAssignableFrom(paramter.getType())) {
        statement.setBigDecimal(element.getKey(), BigDecimal.class.cast(paramter.getValue()));
      } else if (byte[].class.isAssignableFrom(paramter.getType())) {
        statement.setBytes(element.getKey(), byte[].class.cast(paramter.getValue()));
      } else if (Blob.class.isAssignableFrom(paramter.getType())) {
        statement.setBlob(element.getKey(), Blob.class.cast(paramter.getValue()));
      } else {
        throw new IllegalArgumentException(
            "Unsupported object type for QueryParameter: " + paramter.getType());
      }
    }
  }

  /**
   * Transaction.
   *
   * @throws RaSqlException RaSqlException
   * @throws RaConnectException RaConnectException
   */
  @Override
  public void executeTransaction(TransactionExecutor executor)
      throws RaConnectException, RaSqlException {
    if (!isLive()) {
      throw new RaConnectException("Connect to database failed.");
    }

    this.connection.getConnection(
        dbConnection -> {
          boolean ret = false;
          try {
            dbConnection.setAutoCommit(false);

            Transaction tran =
                new Transaction(
                    new StatementFactory() {

                      @SuppressWarnings("unchecked")
                      @Override
                      public <T extends Statement> T create(String sql)
                          throws RaConnectException, SQLException {
                        return sql == null
                            ? (T) dbConnection.createStatement()
                            : (T) dbConnection.prepareStatement(sql);
                      }
                    });
            ret = executor.apply(tran);
          } catch (SQLException e) {
            throw new RaSqlException(e);
          } finally {
            if (ret) {
              try {
                dbConnection.commit();
              } catch (SQLException e) {
                throw new RaSqlException(e);
              }
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
   * Return the last id after executing SQL statement.
   *
   * @param sql SQL statement
   * @return last id
   */
  @Override
  public LastInsertId insert(String sql) {
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
      return lastInsertId(sql);
    } catch (Exception e) {
      throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
    }
  }

  private LastInsertId lastInsertId(String sql) throws SQLException, RaConnectException {
    Reference<LastInsertId> ref = new Reference<>();

    this.connection.getConnection(
        dbConnection -> {
          try {
            dbConnection.setAutoCommit(true);
            try (Statement st = dbConnection.createStatement()) {
              synchronized (st) {
                if (st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS) > 0) {
                  ref.set(buildRecord().getLastInsertId(st));
                }
              }
            }
          } catch (SQLException e) {
            throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
          }
          return 0;
        });

    return ref.isNull() ? new LastInsertId(null) : ref.get();
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
            try {
              dbConnection.setAutoCommit(true);

              try (Statement st = dbConnection.createStatement();
                  ResultSet rs = st.executeQuery(sql)) {
                record.convert(rs);
              }
            } catch (SQLException e) {
              throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
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
   * @author Ray Li
   */
  public class Transaction {
    private StatementFactory statementFactory;

    public Transaction(StatementFactory factory) throws SQLException {
      this.statementFactory = factory;
    }

    /**
     * Execute a SQL using a batch.
     *
     * @param sql sql
     * @return execute count
     * @throws RaSqlException RaSqlException
     */
    public int executeUpdate(String sql) throws RaSqlException {
      try (Statement statement = statementFactory.create(null)) {

        return statement.executeUpdate(sql);
      } catch (SQLException e) {
        throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
      }
    }

    /**
     * The auto-generated keys generated by this Statement object should be available for use in SQL
     * statements that are INSERT statements.
     *
     * @param sql sql
     * @return last id
     * @throws RaSqlException RaSqlException
     */
    public LastInsertId insert(String sql) throws RaSqlException {
      try (Statement statement = statementFactory.create(null)) {

        if (statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS) > 0) {
          return buildRecord().getLastInsertId(statement);
        }
      } catch (SQLException e) {
        throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
      }
      return new LastInsertId(null);
    }

    /**
     * Execute a query SQL using a batch.
     *
     * @param sql sql
     * @return RecordCursor
     * @throws RaSqlException RaSqlException
     */
    public RecordCursor executeQuery(String sql) throws RaSqlException {
      Record record = buildRecord();

      try (Statement statement = statementFactory.create(null)) {
        ResultSet rs = statement.executeQuery(sql);

        record.convert(rs);
      } catch (SQLException e) {
        throw new RaSqlException("SQL Syntax Error, sql=" + sql, e);
      }
      return record;
    }

    /**
     * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     *
     * @param prepared prepared
     * @return RecordCursor
     * @throws RaConnectException RaConnectException
     * @throws RaSqlException RaSqlException
     */
    public int prepareExecuteUpdate(Prepared prepared) throws RaConnectException, RaSqlException {
      try (PreparedStatement statement = statementFactory.create(prepared.getSql())) {

        setParametersPreparedStatement(prepared, statement);

        return statement.executeUpdate();
      } catch (SQLException e) {
        throw new RaSqlException(
            "SQL Syntax Error, sql=" + prepared.getSql() + ",values=" + prepared.getValues(), e);
      }
    }

    /**
     * A SQL statement is precompiled and stored in a Prepared object. This object can then be used
     * to efficiently execute this statement multiple times.
     *
     * @param prepared prepared
     * @return RecordCursor
     * @throws RaConnectException RaConnectException
     * @throws RaSqlException RaSqlException
     */
    public RecordCursor prepareExecuteQuery(Prepared prepared)
        throws RaConnectException, RaSqlException {
      Record record = buildRecord();

      try (PreparedStatement statement = statementFactory.create(prepared.getSql())) {

        setParametersPreparedStatement(prepared, statement);

        try (ResultSet rs = statement.executeQuery()) {
          record.convert(rs);
        }
      } catch (SQLException e) {
        throw new RaSqlException(
            "SQL Syntax Error, sql=" + prepared.getSql() + ",values=" + prepared.getValues(), e);
      }

      return record;
    }
  }
}
