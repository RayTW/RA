package ra.db;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Test;
import ra.db.connection.MockOriginalConnection;
import ra.db.parameter.MysqlParameters;
import ra.db.record.RecordCursor;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;

/** Test class. */
public class JdbcExecutorTest {
  private static final MysqlParameters.Builder MYSQL_PARAM =
      new MysqlParameters.Builder().setHost("127.0.0.1").setPort(1234);

  @Test
  public void testExecuteWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.executeUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testTryExecuteWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.tryExecuteUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testTryExecuteWhenIsLiveTrue() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(true);

    try {
      executor.tryExecuteUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testExecuteCommitWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);
    ArrayList<String> sqls = new ArrayList<>();

    sqls.add("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");

    try {
      executor.executeCommit(sqls);
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testInsertWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.insert("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testPrepareUpdateWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.prepareExecuteUpdate(
          Prepared.newBuilder("UPDATE ... WHERE arg = ?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testPrepareQueryWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.prepareExecuteQuery(
          Prepared.newBuilder("SELECT a,b,c FROM table WHERE name =?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  // TODO 改用H2來測
  @Test
  public void testPrepareQueryFromString() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection
        .getMockConnection()
        .setExecuteQueryListener(
            sql -> {
              MockResultSet result = new MockResultSet("col_string");

              result.addValue("col_string", "col_value");
              return result;
            });

    RecordCursor record =
        executor.prepareExecuteQuery(
            Prepared.newBuilder("SELECT col_string FROM table WHERE name =?;")
                .set(1, ParameterValue.string("args0"))
                .build());

    System.out.println(record);
  }

  @Test
  public void testPrepareUpdateThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testPrepareUpdateThrowException"));

    try {
      executor.prepareExecuteUpdate(
          Prepared.newBuilder("UPDATE ... WHERE arg = ?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testPrepareQueryThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testPrepareQueryThrowException"));

    try {
      executor.prepareExecuteQuery(
          Prepared.newBuilder("SELECT a,b,c FROM table WHERE name =?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }
}
