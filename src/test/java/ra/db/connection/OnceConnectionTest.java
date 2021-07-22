package ra.db.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import ra.db.StatementExecutor;
import ra.db.parameter.DatabaseParameters;
import ra.db.parameter.H2Parameters;
import ra.db.parameter.MysqlParameters;
import ra.db.record.RecordCursor;
import ra.ref.BooleanReference;
import ra.ref.Reference;
import ra.util.Utility;

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
                return new MockResultSet();
              });

      db.createStatementExecutor().executeQuery("SELECT 1");
    }
  }

  @Test
  public void testExecuteQueryUsingForeach() throws Exception {
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

    try (MockResultSet resultSet = new MockResultSet("lastid");
        OnceConnection db =
            new OnceConnection(param) {
              @Override
              public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
                MockConnection connection = new MockConnection();

                connection.setExecuteUpdateListener(
                    actual -> {
                      assertEquals(sql, actual);
                      return 1;
                    });

                resultSet.addValue("lastid", expected);

                connection.setExecuteQueryListener(sql -> resultSet);

                return connection;
              }
            }; ) {

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

    try (MockResultSet resultSet = new MockResultSet("lastid");
        OnceConnection db =
            new OnceConnection(param) {
              @Override
              public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
                MockConnection connection = new MockConnection();

                resultSet.addValue("lastid", expected);

                connection.setExecuteUpdateListener(
                    actual -> {
                      assertEquals(sql, actual);
                      return 1;
                    });

                connection.setExecuteQueryListener(sql -> resultSet);

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
    }
  }

  @Test
  public void testExecuteQueryConnected() throws SQLException {
    String sql = "SELECT * FROM table;";
    Reference<String> actual = new Reference<>();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (MockResultSet resultSet = new MockResultSet("lastid");
        OnceConnection db =
            new OnceConnection(param) {
              @Override
              public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
                MockConnection connection = new MockConnection();
                connection.setExecuteQueryListener(
                    sql -> {
                      actual.set(sql);
                      resultSet.addValue("lastid", 55);

                      return resultSet;
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
            return new MockConnection();
          }
        }) {

      db.createStatementExecutor()
          .executeCommit(
              sqlList, exception -> assertThat(exception, instanceOf(ConnectException.class)));
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
  public void testConnectionFailure() {
    exceptionRule.expect(NullPointerException.class);

    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    try (OnceConnection db =
        new OnceConnection(param) {
          @Override
          public Connection tryGetConnection(DatabaseParameters param) throws SQLException {
            throw new RuntimeException();
          }
        }) {

      assertFalse(db.connect());
    }
  }

  @Test
  public void testConnectionFailureThrowException() {
    BooleanReference ref = new BooleanReference();
    MysqlParameters param =
        new MysqlParameters.Builder().setHost("127.0.0.1").setName("test").build();

    new OnceConnection(param) {
      @Override
      public Connection getConnection() {
        return new MockConnection() {
          @Override
          public void close() throws SQLException {
            super.close();
            ref.set(true);
            throw new SQLException("Unit Test");
          }
        };
      }
    }.close();

    assertTrue(ref.get());
  }

  @Test
  public void testConnectToH2Database() throws SQLException {
    try (OnceConnection connection =
        new OnceConnection(new H2Parameters.Builder().inMemory().setName("test").build())) {
      connection.connect();

      StatementExecutor executor = connection.createStatementExecutor();
      executor.execute(Utility.get().readFile("src/test/resources/mydb.sql"));
      String sql =
          "INSERT INTO DEMO_SCHEMA SET col_int=1"
              + ",col_double=1.01"
              + ",col_boolean=true"
              + ",col_tinyint=5"
              + ",col_enum='enum1'"
              + ",col_decimal=1.1111"
              + ",col_varchar='col_varchar'"
              + ",created_at=NOW();";
      executor.execute(sql);
      executor.execute(sql);
      int actual = executor.insert(sql);

      assertEquals(3, actual);

      executor.execute("DROP TABLE DEMO_SCHEMA");
    }
  }
}
