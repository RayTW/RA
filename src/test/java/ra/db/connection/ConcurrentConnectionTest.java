package ra.db.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.MockConnection;
import ra.db.MockResultSet;
import ra.db.parameter.DatabaseParameters;
import ra.db.parameter.MysqlParameters;
import ra.db.record.LastInsertId;
import ra.db.record.RecordCursor;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;
import ra.ref.Reference;
import ra.util.Utility;

/** Test class. */
public class ConcurrentConnectionTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testConnectUsingInitConnection() {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();
    MockConnection connection = new MockConnection();

    ConcurrentConnection obj =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
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
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            return connection;
          }
        }) {

      db.connectIf(executor -> executor.executeUpdate(sql));
    }
  }

  @Test
  public void testExecuteSqlThrowRaSqlException() {
    exceptionRule.expect(RaSqlException.class);

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    ConcurrentConnection obj =
        new ConcurrentConnection(param) {
          @Override
          public Connection getConnection() {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  throw new RaSqlException();
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
            .executeUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");

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
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {

      try {
        db.connectIf(executor -> executor.executeUpdate(sql));
      } catch (Exception e) {
        assertThat(e, instanceOf(RaConnectException.class));
      }
      assertFalse(db.isLive());
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
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            MockConnection connection = new MockConnection();
            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            return connection;
          }
        }) {
      try {
        db.connectIf(executor -> executor.executeUpdate(sql));
      } catch (Exception e) {
        assertNull(e);
      }
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
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
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
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {

      try {
        db.connectIf(executor -> executor.executeCommit(sqlList));
      } catch (Exception e) {
        assertThat(e, instanceOf(RaConnectException.class));
      }
    }
  }

  @Test
  public void testInsertSqlConnected() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });
            @SuppressWarnings("resource")
            MockResultSet result = new MockResultSet("lastid");

            result.addValue("lastid", 999);

            connection.setExecuteQueryListener(sql -> result);

            return connection;
          }
        }) {

      db.connectIf(
          executor -> {
            LastInsertId actual = executor.insert(sql);

            assertEquals(999, actual.toInt());
          });
    }
  }

  @Test
  public void testInsertSqlDisconnected() {
    int expected = 999;
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
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

      try {
        db.connectIf(executor -> executor.insert(sql));
      } catch (Exception e) {
        assertThat(e, instanceOf(RaConnectException.class));
      }
    }
  }

  @Test
  public void testExecuteQueryConnected() {
    String sql = "SELECT * FROM table;";
    Reference<String> actual = new Reference<>();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
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
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {

      try {
        db.connectIf(executor -> executor.executeQuery(sql));
      } catch (Exception e) {
        assertThat(e, instanceOf(RaConnectException.class));
      }
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

      try {
        db.createStatementExecutor().executeCommit(sqlList);
      } catch (Exception e) {
        assertThat(e, instanceOf(RaConnectException.class));
      }
    }
  }

  @Test
  public void testTryExecute() throws SQLException, ConnectException {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
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

      int result = db.tryExecute(sql);

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
          public Connection getConnection() {
            return null;
          }
        }) {
      db.connect();

      db.tryExecute(sql);
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testOnCheckConnect() throws SQLException {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
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

  @Test
  public void testReconnect() {
    AtomicInteger connectCount = new AtomicInteger(0);
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (ConcurrentConnection db =
        new ConcurrentConnection(param) {
          @Override
          public boolean connect() {
            connectCount.incrementAndGet();
            return super.connect();
          }

          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            MockConnection connection = new MockConnection();

            connection.setExecuteQueryListener(
                sql -> {
                  throw new RaSqlException(sql);
                });

            return connection;
          }
        }) {
      db.connect();
      db.keep();
    }
    assertEquals(2, connectCount.get());
  }
}
