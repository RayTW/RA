package ra.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.naming.NamingException;
import org.junit.Test;
import ra.ref.BooleanReference;
import test.mock.NetObject;

/** Test class. */
public class MessageSenderTest {

  @Test
  public void testBroadcast() throws NamingException {
    String expected = "message";
    MessageSender obj = new MessageSender();
    NetServerApplication application = new NetServerApplication();
    User user = new User();

    user.setIndex(1);

    obj.setNetServiceProvider(application);

    application.putService(
        user.getIndex(),
        new NetObject() {
          @Override
          public void send(String msg) {
            assertEquals(expected, msg);
          }
        });
    application.putUser(user.getIndex(), user);

    obj.broadcast(expected);
  }

  @Test
  public void testSend() throws NamingException {
    String expected = "message";
    MessageSender obj = new MessageSender();
    NetServerApplication application = new NetServerApplication();
    User user = new User();

    user.setIndex(1);

    obj.setNetServiceProvider(application);
    application.putService(
        user.getIndex(),
        new NetObject() {
          @Override
          public void send(String msg) {
            assertEquals(expected, msg);
          }
        });
    application.putUser(user.getIndex(), user);

    obj.send(expected, user.getIndex());
  }

  @Test
  public void testSendClose() throws NamingException {
    String expected = "message";
    MessageSender obj = new MessageSender();
    NetServerApplication application = new NetServerApplication();
    User user = new User();

    user.setIndex(1);

    obj.setNetServiceProvider(application);
    application.putService(
        user.getIndex(),
        new NetObject() {
          @Override
          public void sendClose(String msg) {
            assertEquals(expected, msg);
          }
        });
    application.putUser(user.getIndex(), user);

    obj.sendClose(expected, user.getIndex());
  }

  @Test
  public void testSendWhenThrowRuntimeException() throws NamingException {
    MessageSender obj = new MessageSender();
    NetServerApplication application = new NetServerApplication();
    User user = new User();
    BooleanReference actaul = new BooleanReference(false);

    user.setIndex(1);

    obj.setNetServiceProvider(application);
    application.putService(
        user.getIndex(),
        new NetObject() {
          @Override
          public void send(String msg) {
            throw new RuntimeException();
          }

          @Override
          public void onClose() {
            actaul.set(true);
          }
        });
    application.putUser(user.getIndex(), user);

    obj.send("test", user.getIndex());

    assertTrue(actaul.get());
  }

  @Test
  public void testSendCloseWhenThrowRuntimeException() throws NamingException {
    MessageSender obj = new MessageSender();
    NetServerApplication application = new NetServerApplication();
    User user = new User();
    BooleanReference actaul = new BooleanReference(false);

    user.setIndex(1);

    obj.setNetServiceProvider(application);
    application.putService(
        user.getIndex(),
        new NetObject() {
          @Override
          public void sendClose(String msg) {
            throw new RuntimeException();
          }

          @Override
          public void onClose() {
            actaul.set(true);
          }
        });
    application.putUser(user.getIndex(), user);

    obj.sendClose("test", user.getIndex());

    assertTrue(actaul.get());
  }
}
