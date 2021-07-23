package ra.server.basis;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import ra.net.MessageSender;

/** Test class. */
public class MockSendAdapterTest {

  @Test
  public void testSendUsingJsonObject() {
    MockSendAdapter sd = new MockSendAdapter(new MessageSender());

    sd.send(new JSONObject(), 0);

    String actual = sd.getSendMessgae();

    Assert.assertEquals("{}", actual);
  }

  @Test
  public void testSendUsingString() {
    MockSendAdapter sd = new MockSendAdapter(new MessageSender());

    sd.send("abc", 0);

    String actual = sd.getSendMessgae();

    Assert.assertEquals("abc", actual);
  }

  @Test
  public void testSendCloseUsingJsonObject() {
    MockSendAdapter sd = new MockSendAdapter(new MessageSender());

    sd.sendClose(new JSONObject(), 0);

    String actual = sd.getSendCloseMessgae();

    Assert.assertEquals("{}", actual);
  }

  @Test
  public void testSendCloseUsingString() {
    MockSendAdapter sd = new MockSendAdapter(new MessageSender());

    sd.sendClose("abc", 0);

    String actual = sd.getSendCloseMessgae();

    Assert.assertEquals("abc", actual);
  }

  @Test
  public void testSendWithListener() {
    final Object[] expectedsMethod = {"sendClose", "sendClose", "send", "send"};
    final Object[] expectedsMsg = {"abc", "{}", "111", "{}"};
    Object[] actualMethod = new Object[4];
    Object[] actualMsg = new Object[4];
    AtomicInteger index = new AtomicInteger();
    MockSendAdapter sd = new MockSendAdapter(new MessageSender());

    sd.setSendLstener(
        (method, msg) -> {
          actualMethod[index.get()] = method.getName();
          actualMsg[index.get()] = msg;
          index.incrementAndGet();
        });

    sd.sendClose("abc", 0);
    sd.sendClose(new JSONObject(), 0);
    sd.send("111", 0);
    sd.send(new JSONObject(), 0);

    System.out.println(Arrays.toString(actualMethod));

    Assert.assertArrayEquals(expectedsMethod, actualMethod);
    Assert.assertArrayEquals(expectedsMsg, actualMsg);
  }
}
