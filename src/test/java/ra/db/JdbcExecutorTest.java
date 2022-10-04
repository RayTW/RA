package ra.db;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import org.junit.Test;
import ra.db.connection.MockOriginalConnection;
import ra.exception.RaConnectException;

/** Test class. */
public class JdbcExecutorTest {

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
}
