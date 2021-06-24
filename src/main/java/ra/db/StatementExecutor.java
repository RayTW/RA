package ra.db;

import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;
import ra.db.connection.OnCreatedStatementListener;

/**
 * SQL statement executor.
 *
 * @author Ray Li
 */
public class StatementExecutor {
  private DatabaseConnection connection;

  public StatementExecutor(DatabaseConnection db) {
    connection = db;
  }

  public boolean isLive() {
    return connection.isLive();
  }

  /** 資料庫執行語法，可接受 INSERT, UPDATE, DELETE... 的 SQL 語法. */
  public int execute(String sql) {
    int ret = 0;
    if (!isLive()) {
      System.out.println("[與DB主機無法連線]");
      return ret;
    }

    return execute(sql, e -> e.printStackTrace());
  }

  /**
   * 資料庫執行語法，可接受 INSERT, UPDATE, DELETE... 的 SQL 語法.
   *
   * @param sql 語法
   * @param listener 執行語法時若拋出例外傾聽者
   */
  public int execute(String sql, Consumer<Exception> listener) {
    int ret = 0;
    if (!isLive()) {
      String msg = "[與DB主機無法連線]" + connection.getParam() + "--->" + connection.getConnection();
      System.out.println(msg);

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
   * 測試執行INSERT、UPDATE、DELETE語法是否會成功.
   *
   * @param sql 語法
   */
  public int tryExecute(String sql) {
    return tryExecute(sql, null);
  }

  /**
   * 測試執行INSERT、UPDATE、DELETE語法是否會成功.
   *
   * @param sql 語法
   * @param listener 執行發生錯誤拋出例外
   */
  public int tryExecute(String sql, Consumer<Exception> listener) {
    int ret = 0;

    if (!isLive()) {
      String msg = "[與DB主機無法連線]" + connection.getParam() + "--->" + connection.getConnection();
      System.out.println(msg);

      if (listener != null) {
        listener.accept(new ConnectException(msg));
      }
      return ret;
    }

    return connection.tryExecute(sql, listener);
  }

  /** 接受 insert, delete, setAutoCommit................ 的 SQL 語法 */
  public int executeCommit(List<String> sql) {
    int ret = 0;
    if (!isLive()) {
      System.out.println("[與DB主機無法連線]");
      return ret;
    }

    return executeCommit(sql, e -> e.printStackTrace());
  }

  /**
   * 接受 insert, delete, setAutoCommit的 SQL 語法.
   *
   * @param sql 語法
   * @param listener 執行語法時若拋出例外傾聽者
   */
  public int executeCommit(List<String> sql, Consumer<Exception> listener) {
    int ret = 0;

    if (!isLive()) {
      String msg = "[與DB主機無法連線]" + connection.getParam() + "--->" + connection.getConnection();
      System.out.println(msg);

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
            throw new ConnectException("[與DB主機無法連線]");
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
   * 新增一筆資料後，可取得最後新增的id.
   *
   * @param sql 語法
   */
  public int insert(String sql) {
    return insert(sql, null);
  }

  /**
   * 新增一筆資料後，可取得最後新增的id.
   *
   * @param sql 語法
   * @param errorListener 若執行語法失敗時會拋出ConnectException或SQLException
   */
  public int insert(String sql, Consumer<Exception> errorListener) {
    int ret = 0;

    if (!isLive()) {
      String msg = "[與DB主機無法連線]" + connection.getParam() + "--->" + connection.getConnection();
      System.out.println(msg);

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
          // 採用新的try-close方式關閉Statement與ResultSet。 by Ray
          try (Statement st = dbConnection.createStatement()) {
            synchronized (st) {
              if (st.executeUpdate(sql) > 0) {
                // 採用新的try-close方式關閉Statement與ResultSet。 by Ray
                try (ResultSet rs = st.executeQuery("select last_insert_id() as lastid");
                    RecordSet record = new RecordSet(rs); ) {
                  return Integer.parseInt(record.field("lastid"));
                }
              }
            }
          }
          return 0;
        });
  }

  /**
   * 資料庫查詢，可接受 select ... 的 Query SQL 語法.
   *
   * @param sql 查詢語法
   * @return RecordCursor
   * @throws SQLException 查詢語法有錯誤
   * @throws ConnectException 資料庫沒有連線
   */
  public RecordCursor executeQuery(String sql) {
    return executeQuery(sql, null);
  }

  /**
   * 資料庫查詢，可接受 select ... 的 Query SQL 語法.
   *
   * @param sql 查詢語法
   * @param exceptionListener SQLException 查詢語法有錯誤、ConnectException 資料庫沒有連線
   */
  public RecordCursor executeQuery(String sql, Consumer<Exception> exceptionListener) {
    if (!isLive()) {
      String msg =
          "[與DB主機無法連線]"
              + connection.getParam()
              + ",connect="
              + connection.getConnection()
              + ",sql="
              + sql;

      if (exceptionListener != null) {
        exceptionListener.accept(new ConnectException(msg));
      }

      return new RecordSet();
    }
    RecordSet record = new RecordSet();

    try {
      connection.getConnection(
          dbConnection -> {
            dbConnection.setAutoCommit(true);

            try (Statement st = dbConnection.createStatement(); ) {
              record.executeQuery(connection.getParam(), st, sql);
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

  /** 資料庫查詢(transaction機制)，接受 select ... 的 Query SQL 語法. */
  public void multiQuery(Consumer<MultiQuery> listener) {
    if (!isLive()) {
      System.out.println(
          "[與DB主機無法連線]" + connection.getParam() + ",connect=" + connection.getConnection());
      return;
    }

    try {
      query(
          (st) -> {
            try (MultiQuery multiQuery = new MultiQuery(st); ) {
              listener.accept(multiQuery);
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** 資料庫查詢(transaction機制)，接受 select ... 的 Query SQL 語法. */
  public void multiQuery(Consumer<MultiQuery> listener, Consumer<Exception> exceptionListener) {
    if (!isLive()) {
      System.out.println(
          "[與DB主機無法連線]" + connection.getParam() + "--->" + connection.getConnection());
      return;
    }

    try {
      query(
          (st) -> {
            try (MultiQuery multiQuery = new MultiQuery(st); ) {
              listener.accept(multiQuery);
            }
          });
    } catch (Exception e) {
      if (exceptionListener != null) {
        exceptionListener.accept(e);
      }
    }
  }

  /**
   * 執行查詢語法.
   *
   * @param listener 取得Statement
   * @throws SQLException 執行語法錯誤時拋出
   */
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
}
