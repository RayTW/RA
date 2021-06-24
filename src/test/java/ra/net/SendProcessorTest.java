package ra.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import org.junit.Test;
import test.mock.MockSocket;

/** Test class. */
public class SendProcessorTest {

  @Test
  public void testClose() throws IOException {
    SendProcessor obj = new SendProcessor(null, new MockSocket(), 10000);

    obj.close();

    assertNotNull(obj);
  }

  @Test
  public void testGetIpIsNull() throws IOException {
    SendProcessor obj = new SendProcessor(null);

    assertNull(obj.getIp());
  }

  @Test
  public void testGetSendcompileteIsFalse() throws IOException {
    SendProcessor obj = new SendProcessor(null);

    assertFalse(obj.getSendcompilete());
  }
}
