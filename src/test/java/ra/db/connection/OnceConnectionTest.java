package ra.db.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.DatabaseConnection;
import ra.db.MockConnection;
import ra.db.MockResultSet;
import ra.db.MockStatementExecutor;
import ra.db.RecordCursor;
import ra.db.parameter.DatabaseParameters;
import ra.db.parameter.MysqlParameters;
import ra.ref.Reference;
import ra.util.Utility;
import test.mock.resultset.MockLastidResultSet;

/** Test class. */
public class OnceConnectionTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testConnectUsingInitConnection() {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();
    MockConnection connection = new MockConnection();

    OnceConnection obj =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return connection;
          }
        };

    Utility.get().replaceMember(obj, "connection", new MockConnection());

    assertTrue(obj.connect());
  }

  @Test
  public void testOnceConnectionNewInstance() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder
        .setHost("127.0.0.1")
        .setName("dbName")
        .setPassword("112233")
        .setPort(3306)
        .setUser("user");

    MysqlParameters param = builder.build();

    DatabaseConnection db = new OnceConnection(param);

    assertNotNull(db);
  }

  @Test
  public void testExecuteQuery() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder
        .setHost("127.0.0.1")
        .setName("test")
        .setPassword("passwd")
        .setPort(3306)
        .setUser("user");

    MysqlParameters param = builder.build();

    try (MockOnceConnection db = new MockOnceConnection(param)) {

      db.getMockConnection()
          .setExecuteQueryListener(
              sql -> {
                assertEquals("SELECT 1", sql);
                return null;
              });

      db.createStatementExecutor().executeQuery("SELECT 1");
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  @Test
  public void testExecuteQueryUsingForeach() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder
        .setHost("127.0.0.1")
        .setName("test")
        .setPassword("passwd")
        .setPort(3306)
        .setUser("user");

    MysqlParameters param = builder.build();

    try (DatabaseConnection db = new MockOnceConnection(param)) {

      MockStatementExecutor executor = new MockStatementExecutor(db);

      executor.setFakeQueryColumnsName(new String[] {"name", "age"});
      executor.addFakeQuery(new String[] {"testUser", "1"});

      executor.setOpenListener(
          sql -> {
            assertEquals("select * from user", sql);
          });

      RecordCursor cursor = executor.executeQuery("select * from user");

      cursor.forEach(
          row -> {
            assertEquals(row.getString("name"), "testUser");
            assertEquals(row.getInt("age"), 1);
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testExecuteSql() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
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

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testExecuteSqlThrowRuntimeException() throws SQLException {
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    OnceConnection obj =
        new OnceConnection(param) {
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
  public void testExecuteSqlDisconnected() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testExecuteSqlConnected() {
    String sql =
        "INSERT INTO `user` (`number`, `name`, `age`, `birthday`, `money`) "
            + "VALUES ('1', 'abc', '22', '2019-12-11', '66');";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
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
    } catch (Exception e) {
      e.printStackTrace();
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

    try (OnceConnection db =
        new OnceConnection(param) {
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
    } catch (Exception e) {
      e.printStackTrace();
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

    try (OnceConnection db =
        new OnceConnection(param) {
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
    } catch (Exception e) {
      e.printStackTrace();
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

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            connection.setExecuteQueryListener(sql -> new MockLastidResultSet(expected));

            return connection;
          }
        }) {

      db.connectIf(
          executor -> {
            int actual = executor.insert(sql, exception -> {});

            assertEquals(expected, actual);
          });

    } catch (Exception e) {
      e.printStackTrace();
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

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();

            connection.setExecuteUpdateListener(
                actual -> {
                  assertEquals(sql, actual);
                  return 1;
                });

            connection.setExecuteQueryListener(sql -> new MockLastidResultSet(expected));

            return connection;
          }
        }) {
      db.connectIf(
          executor -> {
            executor.insert(
                sql,
                exception -> {
                  assertThat(exception, instanceOf(ConnectException.class));
                });
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testExecuteQueryConnected() {
    String sql = "SELECT * FROM table;";
    Reference<String> actual = new Reference<>();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();
            connection.setExecuteQueryListener(
                sql -> {
                  actual.set(sql);

                  return new MockLastidResultSet(55);
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

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testExecuteQueryDisconnected() {
    String sql = "SELECT * FROM table;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
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
    } catch (Exception e) {
      e.printStackTrace();
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

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection getConnection() {
            return null;
          }
        }) {

      db.createStatementExecutor()
          .executeCommit(
              sqlList, exception -> assertThat(exception, instanceOf(ConnectException.class)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTryExecute() {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTryExecuteDisconnected() {
    String sql = "UPDATE tableName SET 'field1' = value WHERE 1;";
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            return null;
          }
        }) {
      db.connect();

      int result =
          db.tryExecute(
              sql, exception -> assertThat(exception, instanceOf(ConnectException.class)));

      assertEquals(0, result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testOnCheckConnectThrowUnsupportedOperationException() {
    exceptionRule.expect(UnsupportedOperationException.class);

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            MockConnection connection = new MockConnection();

            connection.setExecuteQueryListener(
                sql -> {
                  assertEquals("select 1", sql);
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
