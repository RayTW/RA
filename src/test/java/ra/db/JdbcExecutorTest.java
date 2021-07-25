package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Test;
import ra.db.connection.MockConcurrentConnection;
import ra.db.connection.MockOriginalConnection;
import ra.db.parameter.MysqlParameters;
import ra.db.record.RecordCursor;
import ra.ref.BooleanReference;

/** Test class. */
public class JdbcExecutorTest {

  @Test
  public void testExecuteWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    int actual = executor.execute("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");

    assertEquals(0, actual);
  }

  @Test
  public void testTryExecuteWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    int actual =
        executor.tryExecute("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);", Optional::of);

    assertEquals(0, actual);
  }

  @Test
  public void testTryExecuteWhenIsLiveTrue() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(true);

    int actual =
        executor.tryExecute("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);", Optional::of);

    assertEquals(0, actual);
  }

  @Test
  public void testExecuteCommitWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);
    ArrayList<String> sqls = new ArrayList<>();

    sqls.add("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");

    int actual = executor.executeCommit(sqls);

    assertEquals(0, actual);
  }

  @Test
  public void testInsertWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    int actual =
        executor.insert("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);", Optional::of);

    assertEquals(0, actual);
  }

  @Test
  public void testMultiQueryWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);
    BooleanReference ref = new BooleanReference(false);

    connection.setIsLive(false);

    executor.multiQuery(
        exec -> {
          try {
            exec.executeQuery("SELECT 1");
            ref.set(true);

          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
    assertFalse(ref.get());
  }

  @Test
  public void testMultiQueryExecptionListenerWhenIsLiveTrue() {
    MockConcurrentConnection connection = new MockConcurrentConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    try (MockResultSet result = new MockResultSet("lastid")) {
      result.addValue("lastid", 22);
      connection.setIsLive(true);
      connection.getMockConnection().setExecuteQueryListener(sql -> result);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    executor.multiQuery(
        exec -> {
          try {
            RecordCursor record = exec.executeQuery("SELECT 1");

            assertEquals("22", record.fieldFeedback("lastid", null));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        },
        Optional::of);
  }

  @Test
  public void testMultiQueryExecptionListenerWhenIsLiveFalse() {
    MockConcurrentConnection connection = new MockConcurrentConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    executor.multiQuery(
        exec -> {
          try {
            exec.executeQuery("SELECT 1");
          } catch (SQLException e) {
            e.printStackTrace();
          }
        },
        Optional::of);
  }

  @Test
  public void testMultiQueryWhenIsLiveTrue() throws SQLException {
    try (MockConcurrentConnection connection =
            new MockConcurrentConnection(new MysqlParameters.Builder().build());
        MockResultSet result = new MockResultSet("lastid")) {

      result.addValue("lastid", 22);
      connection.setIsLive(true);
      connection.getMockConnection().setExecuteQueryListener(sql -> result);

      StatementExecutor executor = new JdbcExecutor(connection);

      executor.multiQuery(
          exec -> {
            try {
              RecordCursor record = exec.executeQuery("SELECT 1");

              assertEquals("22", record.fieldFeedback("lastid", null));
            } catch (SQLException e) {
              e.printStackTrace();
            }
          });
    }
  }
}
