package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import org.junit.Test;

/** Test class. */
public class MockStatementTest {

  @Test
  public void testAllMethod() throws SQLException {
    MockStatement mockStatement = new MockStatement();

    mockStatement.addBatch("");
    mockStatement.cancel();
    mockStatement.clearBatch();
    mockStatement.clearWarnings();
    mockStatement.close();
    mockStatement.closeOnCompletion();
    mockStatement.setCursorName("");
    mockStatement.setEscapeProcessing(false);
    mockStatement.setFetchDirection(0);
    mockStatement.setFetchSize(0);
    mockStatement.setMaxFieldSize(0);
    mockStatement.setMaxRows(0);
    mockStatement.setPoolable(false);
    mockStatement.setQueryTimeout(0);
    mockStatement.enableStreamingResults();
    mockStatement.disableStreamingResults();
    mockStatement.setLocalInfileInputStream(null);
    mockStatement.setPingTarget(null);
    mockStatement.removeOpenResultSet(null);
    mockStatement.setHoldResultsOpenOverClose(false);
    mockStatement.setExecuteListener(null);

    assertEquals(0, mockStatement.getId());
    assertEquals(0, mockStatement.getOpenResultSetCount());
    assertNull(mockStatement.getExceptionInterceptor());
    assertNull(mockStatement.getLocalInfileInputStream());
    assertNull(mockStatement.unwrap(Object.class));
    assertFalse(mockStatement.isWrapperFor(Object.class));
    assertFalse(mockStatement.isPoolable());
    assertFalse(mockStatement.isClosed());
    assertFalse(mockStatement.isCloseOnCompletion());
    assertNull(mockStatement.getWarnings());
    assertEquals(0, mockStatement.getUpdateCount());
    assertEquals(0, mockStatement.getResultSetType());
    assertEquals(0, mockStatement.getResultSetHoldability());
    assertEquals(0, mockStatement.getResultSetConcurrency());
    assertNull(mockStatement.getResultSet());
    assertEquals(0, mockStatement.getQueryTimeout());
    assertFalse(mockStatement.getMoreResults());
    assertFalse(mockStatement.getMoreResults(0));
    assertEquals(0, mockStatement.getMaxRows());
    assertEquals(0, mockStatement.getMaxFieldSize());
    assertNull(mockStatement.getGeneratedKeys());
    assertNotNull(mockStatement.executeQuery(null));
    assertEquals(0, mockStatement.executeUpdate(null));
    assertEquals(0, mockStatement.executeUpdate(null, 0));
    assertEquals(0, mockStatement.executeUpdate(null, new int[] {}));
    assertEquals(0, mockStatement.executeUpdate(null, new String[] {}));
    assertNull(mockStatement.getConnection());
    assertEquals(0, mockStatement.getFetchDirection());
    assertEquals(0, mockStatement.getFetchSize());
    assertNull(mockStatement.executeBatch());
    assertTrue(mockStatement.execute(null));
    assertTrue(mockStatement.execute(null, 0));
    assertTrue(mockStatement.execute(null, new int[] {}));
    assertTrue(mockStatement.execute(null, new String[] {}));
  }
}
