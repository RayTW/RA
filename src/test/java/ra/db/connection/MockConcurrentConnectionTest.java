package ra.db.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ra.db.parameter.MysqlParameters;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class MockConcurrentConnectionTest {

  private static final MysqlParameters.Builder MYSQL_PARAMS =
      new MysqlParameters.Builder().setHost("127.0.0.1").setPort(1234);

  @Test
  public void testMockConcurrentConnectionFromParam() {
    MockConcurrentConnection con = new MockConcurrentConnection(MYSQL_PARAMS.build());

    assertNotNull(con.getParam());
    assertNotNull(con.getMockConnection());
    assertNotNull(con.getConnection());
    assertTrue(con.connect());
  }

  @Test
  public void testMockConcurrentConnectionLiveTrue() {
    MockConcurrentConnection con = new MockConcurrentConnection(MYSQL_PARAMS.build());

    con.setIsLive(true);

    assertTrue(con.isLive());
  }

  @Test
  public void testMockConcurrentConnectionLiveFalse() {
    MockConcurrentConnection con = new MockConcurrentConnection(MYSQL_PARAMS.build());

    con.setIsLive(false);

    assertFalse(con.isLive());
  }
}
