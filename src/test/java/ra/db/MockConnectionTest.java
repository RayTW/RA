package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Map;
import java.util.concurrent.Executor;
import org.junit.Test;

/** Test class. */
public class MockConnectionTest {

  @Test
  public void testAllMethod() throws SQLException {
    MockConnection connection = new MockConnection();

    Map<String, Class<?>> map = null;
    connection.setTypeMap(map);
    connection.setTransactionIsolation(0);
    connection.setSchema("");
    connection.setReadOnly(false);
    Executor executor = null;
    connection.setNetworkTimeout(executor, 0);
    connection.setHoldability(0);
    connection.setClientInfo("", "");
    connection.setClientInfo(null);
    connection.setCatalog("");
    connection.setAutoCommit(false);
    Savepoint savepoint = null;
    connection.rollback(savepoint);
    connection.rollback();
    connection.releaseSavepoint(savepoint);
    connection.commit();
    connection.close();
    connection.clearWarnings();
    connection.abort(null);
    connection.setExecuteUpdateListener(null);
    connection.setExecuteQueryListener(null);
    connection.setExecuteListener(null);

    assertNull(connection.setSavepoint(""));
    assertNull(connection.setSavepoint());
    assertNull(connection.prepareCall("", 0, 0, 0));
    assertNull(connection.prepareCall("", 0, 0));
    assertNull(connection.prepareCall(""));
    assertNotNull(connection.prepareStatement("", 0, 0, 0));
    assertNotNull(connection.prepareStatement("", 0, 0));
    assertNotNull(connection.prepareStatement("", 0));
    assertNotNull(connection.prepareStatement("", new String[] {}));
    assertNotNull(connection.prepareStatement("", new int[] {}));
    assertNotNull(connection.prepareStatement(""));
    assertNull(connection.nativeSQL(""));
    assertFalse(connection.isValid(0));
    assertFalse(connection.isReadOnly());
    assertFalse(connection.isClosed());
    assertNull(connection.getWarnings());
    assertNull(connection.getTypeMap());
    assertEquals(0, connection.getTransactionIsolation());
    assertNull(connection.getSchema());
    assertEquals(0, connection.getNetworkTimeout());
    assertNull(connection.getMetaData());
    assertEquals(0, connection.getHoldability());
    assertNull(connection.getClientInfo(""));
    assertNull(connection.getClientInfo());
    assertNull(connection.getCatalog());
    assertNull(connection.createSQLXML());
    assertNull(connection.createNClob());
    assertNull(connection.createClob());
    assertNull(connection.createBlob());
    assertNull(connection.createArrayOf("", null));
    assertNull(connection.unwrap(null));
    assertFalse(connection.isWrapperFor(null));
    assertNotNull(connection.createStatement(0, 0, 0));
    assertNotNull(connection.createStatement(0, 0));
    assertNotNull(connection.createStatement());
    assertNull(connection.createStruct("", null));
    assertFalse(connection.getAutoCommit());
  }
}
