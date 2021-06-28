package ra.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.naming.NamingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.net.processor.DataNetCommandProvider;
import ra.net.processor.NetCommandProvider;
import ra.util.annotation.Configuration;
import ra.util.annotation.ServerApplication;
import test.UnitTestUtils;
import test.mock.NetObject;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class NetServerApplicationTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testGetUser() {
    NetServerApplication obj = new NetServerApplication();
    DefaultUser user = new DefaultUser();

    user.setIndex(1);

    obj.putUser(1, user);

    assertEquals(user, obj.getUser(1));
  }

  @Test
  public void testUserOffline() {
    NetServerApplication obj = new NetServerApplication();
    DefaultUser user = new DefaultUser();

    user.setIndex(1);
    obj.putUser(1, user);
    obj.removeUser(1);

    assertNull(obj.getUser(1));
  }

  @Test
  public void testPutService() throws NamingException {
    NetServerApplication obj = new NetServerApplication();
    NetObject net = new NetObject();

    obj.putService("abc", net);

    assertEquals(net, obj.getService("abc"));
  }

  @Test
  public void testPutServiceThrowNamingException() throws NamingException {
    exceptionRule.expect(NamingException.class);
    exceptionRule.expectMessage("Naming repeat, key = abc");

    NetServerApplication obj = new NetServerApplication();
    NetObject net = new NetObject();

    obj.putService("abc", net);
    obj.putService("abc", net);
  }

  @Test
  public void testRunNotServerApplication() throws NamingException {
    exceptionRule.expect(RuntimeException.class);
    exceptionRule.expectMessage(
        "Source 'class ra.net.NetServerApplicationTest$1FakeServer' is not "
            + "annotation @ServerApplication.");
    class FakeServer {}

    NetServerApplication.run(FakeServer.class);
  }

  @Test
  public void testRunNotConfiguration() throws NamingException {
    exceptionRule.expect(RuntimeException.class);
    exceptionRule.expectMessage(
        "Source 'class ra.net.NetServerApplicationTest$1TestRunNotConfiguration'"
            + " is not annotation @Configuration.");
    @ServerApplication(serviceMode = NetCommandProvider.class)
    class TestRunNotConfiguration {}

    NetServerApplication.run(TestRunNotConfiguration.class);
  }

  @Test
  public void testRunNetServiceSuccess() throws NamingException, IOException {
    @ServerApplication(serviceMode = NetCommandProvider.class)
    @Configuration("unittest/temp.properties")
    class TestRunNetServiceSuccess {}

    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(1);
    int serverPort = serverSocket.getLocalPort();

    serverSocket.close();

    Path path =
        UnitTestUtils.createTempPropertiesFile(
            "unittest/temp.properties",
            properties -> {
              properties.put("server.netservice.max-threads", "1");
              properties.put("server.port", String.valueOf(serverPort));
            });
    NetServerApplication.run(TestRunNetServiceSuccess.class);
    Files.delete(path);

    assertNotNull(NetServerApplication.getApplication());
    assertNotNull(NetServerApplication.getApplication().getMessageSender());
  }

  @Test
  public void testRunDataNetServiceSuccess() throws NamingException, IOException {
    @ServerApplication(serviceMode = DataNetCommandProvider.class)
    @Configuration("unittest/temp2.properties")
    class TestRunDataNetServiceSuccess {}

    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(1);
    int serverPort = serverSocket.getLocalPort();

    serverSocket.close();

    Path path =
        UnitTestUtils.createTempPropertiesFile(
            "unittest/temp2.properties",
            properties -> {
              properties.put("server.netservice.max-threads", "1");
              properties.put("server.port", String.valueOf(serverPort));
            });
    NetServerApplication.run(TestRunDataNetServiceSuccess.class);
    Files.delete(path);

    assertNotNull(NetServerApplication.getApplication());
    assertNotNull(NetServerApplication.getApplication().getMessageSender());
  }
}
