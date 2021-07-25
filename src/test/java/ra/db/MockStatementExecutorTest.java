package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;
import ra.db.record.RecordCursor;

/** Test class. */
public class MockStatementExecutorTest {

  @Test
  public void testFakeQuery() {
    MockStatementExecutor executor = new MockStatementExecutor();

    executor.setFakeQueryColumnsNameList(Arrays.asList("id", "name"));
    executor.addFakeQuery(new String[] {"1", "testUser"});
    RecordCursor record = executor.executeQuery("SELECT * FROM table;");

    assertEquals(record.field("name"), "testUser");

    executor.clearFakeQuery();
  }

  @Test
  public void testClearFakeQuery() {
    MockStatementExecutor executor = new MockStatementExecutor();

    executor.setFakeQueryColumnsNameList(Arrays.asList("id", "name"));
    executor.addFakeQuery(new String[] {"1", "testUser"});
    executor.clearFakeQuery();
    RecordCursor record = executor.executeQuery("SELECT * FROM table;");

    assertEquals(0, record.getRecordCount());
  }

  @Test
  public void testInsert() {
    MockStatementExecutor executor = new MockStatementExecutor();
    int expected = 1;

    executor.setInsertListener(sql -> expected);

    int actual = executor.insert("INSERT INTO table_name VALUES (value1, value2);");

    assertEquals(expected, actual);
  }

  @Test
  public void testInsertListenExecption() {
    MockStatementExecutor executor = new MockStatementExecutor();
    int expected = 1;

    executor.setInsertListener(sql -> expected);

    int actual = executor.insert("INSERT INTO table_name VALUES (value1, value2);", null);

    assertEquals(expected, actual);
  }

  @Test
  public void testTryExecute() {
    MockStatementExecutor executor = new MockStatementExecutor();
    int expected = 1;

    executor.setTryExecuteListener(sql -> expected);

    int actual = executor.tryExecute("INSERT INTO table_name VALUES (value1, value2);");

    assertEquals(expected, actual);
  }

  @Test
  public void testTryExecuteListenExecption() {
    MockStatementExecutor executor = new MockStatementExecutor();
    int expected = 1;

    executor.setTryExecuteListener(sql -> expected);

    int actual = executor.tryExecute("INSERT INTO table_name VALUES (value1, value2);", null);

    assertEquals(expected, actual);
  }

  @Test
  public void testExecute() {
    MockStatementExecutor executor = new MockStatementExecutor();
    int expected = 1;

    executor.setExecuteListener(sql -> expected);

    int actual = executor.execute("INSERT INTO table_name VALUES (value1, value2);");

    assertEquals(expected, actual);
  }

  @Test
  public void testExecuteListenExecption() {
    MockStatementExecutor executor = new MockStatementExecutor();
    int expected = 1;

    executor.setExecuteListener(sql -> expected);

    int actual = executor.execute("INSERT INTO table_name VALUES (value1, value2);", null);

    assertEquals(expected, actual);
  }

  @Test
  public void testIsLive() {
    MockStatementExecutor executor = new MockStatementExecutor();

    executor.setIsLive(true);

    assertTrue(executor.isLive());
  }
}
