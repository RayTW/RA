package ra.mock.server.basis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.function.BiConsumer;
import org.json.JSONObject;
import org.junit.Test;
import ra.net.MessageSender;
import ra.net.UserListener;
import ra.server.basis.SenderAdapter;
import ra.util.logging.LogEveryDay;
import ra.util.logging.LogSettings;

/** Test class. */
public class SenderAdapterTest {
  LogEveryDay mockLog = getMockLog();

  @Test
  public void testBoardcast() {
    String expected = "expected";
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("boardcast"));
          assertEquals(expected, message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.boardcast(expected);
  }

  @Test
  public void testBoardcastList() {
    String expected = "expected";
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("boardcast"));
          assertEquals(expected, message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.boardcast(expected, null);
  }

  @Test
  public void testSend() {
    String expected = "expected";
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("send"));
          assertEquals(expected, message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.send(expected, 0);
  }

  @Test
  public void testJsonSend() {
    JSONObject expected = new JSONObject();
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("send"));
          assertEquals(expected.toString(), message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.send(expected, 0);
  }

  @Test
  public void testSendClose() {
    String expected = "expected";
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("sendClose"));
          assertEquals(expected, message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.sendClose(expected, 0);
  }

  @Test
  public void testJsonSendClose() {
    JSONObject expected = new JSONObject();
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("sendClose"));
          assertEquals(expected.toString(), message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.sendClose(expected, 0);
  }

  @Test
  public void testSendError() {
    String expected = "expected";
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("send"));
          assertEquals("{\"code\":100,\"message\":\"expected\"}", message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.sendError("", 100, expected, 0);
  }

  @Test
  public void testMessageNullSendError() {
    String expected = null;
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("send"));
          assertEquals("{\"code\":100}", message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.sendError("", 100, expected, 0);
  }

  @Test
  public void testSendErrorClose() {
    String expected = "expected";
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("sendClose"));
          assertEquals("{\"code\":100,\"message\":\"expected\"}", message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.sendErrorClose("", 100, expected, 0);
  }

  @Test
  public void testMessageNullSendErrorClose() {
    String expected = null;
    BiConsumer<String, String> back =
        (functionName, message) -> {
          assertTrue(functionName.equals("sendClose"));
          assertEquals("{\"code\":100}", message);
        };
    MockSend send = new MockSend(back);
    SenderAdapter sendAdapter = new SenderAdapter(send, mockLog, mockLog);
    sendAdapter.sendErrorClose("", 100, expected, 0);
  }

  private LogEveryDay getMockLog() {
    String filePath = "./log/loggingTest";
    LogSettings setting = new LogSettings();

    setting.setPath(filePath);

    LogEveryDay log = new LogEveryDay(false, setting, "UTF-8");
    log.setLogEnable(false);
    return log;
  }

  class MockSend extends MessageSender {
    BiConsumer<String, String> callBack;

    public MockSend(BiConsumer<String, String> callBack) {
      this.callBack = callBack;
    }

    @Override
    public <T extends UserListener> void boardcast(String obj, Map<String, T> userlist) {
      callBack.accept(new Object() {}.getClass().getEnclosingMethod().getName(), obj);
    }

    @Override
    public void boardcast(String obj) {
      callBack.accept(new Object() {}.getClass().getEnclosingMethod().getName(), obj);
    }

    @Override
    public void send(String obj, int index) {
      callBack.accept(new Object() {}.getClass().getEnclosingMethod().getName(), obj);
    }

    @Override
    public void sendClose(String obj, int index) {
      callBack.accept(new Object() {}.getClass().getEnclosingMethod().getName(), obj);
    }
  }
}
