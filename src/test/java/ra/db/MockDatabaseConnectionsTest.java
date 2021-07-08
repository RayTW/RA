package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import org.junit.Test;
import ra.db.parameter.DatabaseParameters;
import ra.ref.BooleanReference;

/** Test class. */
public class MockDatabaseConnectionsTest {

  @Test
  public void testNewInstanceNoName() {
    MockDatabaseConnections obj = new MockDatabaseConnections();

    assertNotNull(obj.getStatementExecutor(0));
  }

  @Test
  public void testNewInstanceUseName() {
    MockDatabaseConnections obj = new MockDatabaseConnections("name");

    assertEquals("name", obj.getName());
  }

  @Test
  public void testConnectOriginalConnection() {
    BooleanReference result = new BooleanReference(false);
    MockDatabaseConnections obj =
        new MockDatabaseConnections() {
          @Override
          public void connectOriginalConnection(DatabaseParameters param, int count) {
            super.connectOriginalConnection(param, count);
            result.set(true);
          }
        };

    obj.connectOriginalConnection(null, 0);

    assertTrue(result.get());
  }

  @Test
  public void testConnectConcurrentConnection() {
    BooleanReference result = new BooleanReference(false);
    MockDatabaseConnections obj =
        new MockDatabaseConnections() {
          @Override
          public void connectConcurrentConnection(DatabaseParameters param, int count) {
            super.connectOriginalConnection(param, count);
            result.set(true);
          }
        };

    obj.connectConcurrentConnection(null, 0);

    assertTrue(result.get());
  }

  @Test
  public void testConnect() throws Exception {
    BooleanReference result = new BooleanReference(false);
    MockDatabaseConnections obj =
        new MockDatabaseConnections() {
          @Override
          public void connect(
              DatabaseParameters param, int count, Callable<DatabaseConnection> connectionMode)
              throws Exception {
            super.connect(param, count, connectionMode);
            result.set(true);
          }
        };

    obj.connect(null, 0, null);

    assertTrue(result.get());
  }

  @Test
  public void testMockStatementExecutorSetNull() {
    MockDatabaseConnections obj = new MockDatabaseConnections();

    obj.setMockStatementExecutor(null);

    assertNull(obj.getStatementExecutor(0));
  }

  @Test
  public void testNext() {
    MockDatabaseConnections obj = new MockDatabaseConnections();

    assertNotNull(obj.next());
  }

  @Test
  public void testGetConnection() {
    MockDatabaseConnections obj = new MockDatabaseConnections();

    assertNull(obj.getConnection(0));
  }

  @Test
  public void testClose() throws Exception {
    BooleanReference result = new BooleanReference(false);
    MockDatabaseConnections obj =
        new MockDatabaseConnections() {
          @Override
          public void close() {
            super.close();
            result.set(true);
          }
        };

    obj.close();

    assertTrue(result.get());
  }
}
