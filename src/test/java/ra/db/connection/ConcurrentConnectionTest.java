package ra.db.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Test;
import ra.db.MockConnection;
import ra.db.MockResultSet;
import ra.db.parameter.DatabaseParameters;
import ra.db.parameter.MysqlParameters;
import ra.db.record.RecordCursor;
import ra.ref.Reference;
import ra.util.Utility;

/** Test class. */
public class ConcurrentConnectionTest {

  @Test
  public void testConnectUsingInitConnection() {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();
    MockConnection connection = new MockConnection();

    ConcurrentConnection obj =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return connection;
          }
        };

    Utility.get().replaceMember(obj, "connection", new MockConnection());

    assertTrue(obj.connect());
  }

  @Test
  public void testExecuteSql() throws SQLException {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            return connection;
          }
        }) {

      db.connectIf(executor -> executor.execute(sql));
    }
  }

  @Test
  public void testExecuteSqlThrowRuntimeException() throws SQLException {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    ConcurrentConnection obj =
        new ConcurrentConnection(param) {
          @Override
          public Connection getConnection() {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  throw new RuntimeException();
                });

            return connection;
          }

          @Override
          public boolean isLive() {
            return true;
          }
        };

    int actual =
        obj.createStatementExecutor()
            .execute(
                "INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);",
                exception -> assertThat(exception, instanceOf(RuntimeException.class)));

    obj.close();

    assertEquals(0, actual);
  }

  @Test
  public void testExecuteSqlDisconnected() throws SQLException {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {

      db.connectIf(
          executor ->
              executor.execute(
                  sql,
                  exception -> {
                    assertThat(exception, instanceOf(ConnectException.class));
                  }));
    }
  }

  @Test
  public void testExecuteSqlConnected() throws SQLException {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            return connection;
          }
        }) {
      db.connectIf(executor -> executor.execute(sql, exception -> {}));
    }
  }

  @Test
  public void testExecuteCommitSqlConnected() throws SQLException {
    ArrayList<String> sqlList = new ArrayList<>();
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";

    sqlList.add(sql.toString());
    sqlList.add(sql.toString());

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            return connection;
          }
        }) {
      db.connectIf(executor -> executor.executeCommit(sqlList));
    }
  }

  @Test
  public void testExecuteCommitSqlDisconnected() throws SQLException {
    ArrayList<String> sqlList = new ArrayList<>();
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";

    sqlList.add(sql.toString());
    sqlList.add(sql.toString());

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {
      db.connectIf(
          executor ->
              executor.executeCommit(
                  sqlList,
                  exception -> {
                    assertThat(exception, instanceOf(ConnectException.class));
                  }));
    }
  }

  @Test
  public void testInsertSqlConnected() throws SQLException {
    int expected = 999;
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });
            @SuppressWarnings("resource")
            MockResultSet result = new MockResultSet("lastid");

            result.addValue("lastid", expected);

            connection.setExecuteQueryListener(sql -> result);

            return connection;
          }
        }) {

      db.connectIf(
          executor -> {
            int actual = executor.insert(sql, exception -> {});

            assertEquals(expected, actual);
          });
    }
  }

  @Test
  public void testInsertSqlDisconnected() throws SQLException {
    int expected = 999;
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            @SuppressWarnings("resource")
            MockResultSet result = new MockResultSet("lastid");

            result.addValue("lastid", expected);

            connection.setExecuteQueryListener(sql -> result);

            return connection;
          }
        }) {
      db.connectIf(
          executor ->
              executor.insert(
                  sql, exception -> assertThat(exception, instanceOf(ConnectException.class))));
    }
  }

  @Test
  public void testExecuteQueryConnected() throws SQLException {
    String sql = "SELECT * FROM table;";
    Reference<String> actual = new Reference<>();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();
            connection.setExecuteQueryListener(
                sql -> {
                  actual.set(sql);

                  MockResultSet result = new MockResultSet("lastid");

                  result.addValue("lastid", 55);

                  return result;
                });
            return connection;
          }
        }) {

      db.connectIf(
          executor -> {
            RecordCursor record = executor.executeQuery(sql);

            assertEquals(sql, actual.get());
            assertEquals("55", record.field("lastid"));
          });
    }
  }

  @Test
  public void testExecuteQueryDisconnected() throws SQLException {
    String sql = "SELECT * FROM table;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {
      db.connectIf(
          executor -> {
            executor.executeQuery(
                sql,
                exception -> {
                  assertThat(exception, instanceOf(ConnectException.class));
                });
          });
    }
  }

  @Test
  public void testExecuteCommitConnectionIsNull() throws SQLException {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    ArrayList<String> sqlList = new ArrayList<>();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    sqlList.add(sql.toString());
    sqlList.add(sql.toString());

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection getConnection() {
            return new MockConnection();
          }
        }) {

      db.createStatementExecutor()
          .executeCommit(
              sqlList, exception -> assertThat(exception, instanceOf(ConnectException.class)));
    }
  }

  @Test
  public void testTryExecute() throws SQLException {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            return connection;
          }
        }) {
      db.connect();

      int result = db.tryExecute(sql, exception -> {});

      assertEquals(1, result);
    }
  }

  @Test
  public void testTryExecuteDisconnected() throws SQLException {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return new MockConnection();
          }
        }) {
      db.connect();

      int result =
          db.tryExecute(
              sql, exception -> assertThat(exception, instanceOf(ConnectException.class)));

      assertEquals(0, result);
    }
  }

  @Test
  public void testOnCheckConnect() throws SQLException {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();

            connection.setExecuteQueryListener(
                sql -> {
                  assertEquals("SELECT 1", sql);
                  return new MockResultSet();
                });

            return connection;
          }
        }) {
      db.connect();

      db.keep();
    }
  }
}
