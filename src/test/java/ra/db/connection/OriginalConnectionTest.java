package ra.db.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.DatabaseConnection;
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
public class OriginalConnectionTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testConnectUsingInitConnection() {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();
    MockConnection connection = new MockConnection();

    OriginalConnection obj =
        new OriginalConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            return connection;
          }
        };

    Utility.get().replaceMember(obj, "connection", new MockConnection());

    assertTrue(obj.connect());
  }

  @Test
  public void testAddProperties() {
    MysqlParameters param =
        new MysqlParameters.Builder()
            .setHost("127.0.0.1")
            .setName("dbName")
            .setUser("dbUser")
            .setPassword("dbPassword")
            .setProperties(
                () -> {
                  Properties properties = new Properties();

                  properties.put("tttt", "aabbcc");
                  properties.put("tttt2", "dd11");

                  return properties;
                })
            .build();

    DatabaseConnection db = new OriginalConnection(param);

    assertNotNull(db);
  }

  @Test
  public void testExecuteSql() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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

    OriginalConnection obj =
        new OriginalConnection(param) {
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
  public void testExecuteSqlDisconnected() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testExecuteSqlConnected() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testExecuteCommitSqlConnected() {
    ArrayList<String> sqlList = new ArrayList<>();
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";

    sqlList.add(sql.toString());
    sqlList.add(sql.toString());

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testExecuteCommitSqlDisconnected() {
    ArrayList<String> sqlList = new ArrayList<>();
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";

    sqlList.add(sql.toString());
    sqlList.add(sql.toString());

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            return new MockConnection();
          }

          @Override
          public boolean isLive() {
            return false;
          }
        }) {
      db.connectIf(executor -> executor.executeCommit(sqlList));
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testInsertSqlConnected() {
    int expected = 999;
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            connection.setExecuteQueryListener(
                sql -> {
                  MockResultSet result = new MockResultSet("lastid");

                  result.addValue("lastid", expected);
                  return result;
                });

            return connection;
          }
        }) {
      db.connectIf(
          executor -> {
            LastInsertId actual = executor.insert(sql);

            assertEquals(expected, actual.toInt());
          });
    }
  }

  @Test
  public void testInsertSqlDisconnected() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws RaSqlException {
            @SuppressWarnings("resource")
            MockResultSet result = new MockResultSet("lastid");

            result.addValue("lastid", 11);
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

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

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testExecuteQueryDisconnected() {
    String sql = "SELECT * FROM table;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testExecuteCommitConnectionIsNull() {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    ArrayList<String> sqlList = new ArrayList<>();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    sqlList.add(sql.toString());
    sqlList.add(sql.toString());

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testTryExecute() {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testTryExecuteDisconnected() {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
  public void testOnCheckConnect() throws RaSqlException {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OriginalConnection db =
        new OriginalConnection(param) {
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

    try (OriginalConnection db =
        new OriginalConnection(param) {
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
