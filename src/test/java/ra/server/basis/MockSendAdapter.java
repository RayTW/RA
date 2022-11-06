package ra.server.basis;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;
import org.json.JSONException;
import org.json.JSONObject;
import ra.net.MessageSender;
import ra.net.User;
import ra.util.logging.LogEveryDay;

/**
 * Mock {@link SenderAdapter}.
 *
 * @author Ray Li
 */
public class MockSendAdapter extends SenderAdapter {
  private String sendCloseMsg;
  private String sendMsg;
  private BiConsumer<Method, String> sendListener;

  /**
   * initialize.
   *
   * @param sender sender
   */
  public MockSendAdapter(MessageSender sender) {
    super(sender);
  }

  /**
   * initialize.
   *
   * @param sender sender
   * @param commonLog commonLog
   * @param errorLog errorLog
   */
  public MockSendAdapter(MessageSender sender, LogEveryDay commonLog, LogEveryDay errorLog) {
    super(sender, commonLog, errorLog);
  }

  /**
   * Set listener.
   *
   * @param listener listener
   */
  public void setSendLstener(BiConsumer<Method, String> listener) {
    sendListener = listener;
  }

  @Override
  public void send(String obj, int index) {
    sendMsg = obj;

    if (sendListener != null) {
      try {
        sendListener.accept(
            MockSendAdapter.class.getDeclaredMethod("send", String.class, Integer.TYPE), sendMsg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void send(JSONObject json, int index) {
    sendMsg = "" + json;
    if (sendListener != null) {
      try {
        sendListener.accept(
            MockSendAdapter.class.getDeclaredMethod("send", JSONObject.class, Integer.TYPE),
            sendMsg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void sendClose(String obj, int index) {
    sendCloseMsg = obj;
    if (sendListener != null) {
      try {
        sendListener.accept(
            MockSendAdapter.class.getDeclaredMethod("sendClose", String.class, Integer.TYPE),
            sendCloseMsg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void sendClose(JSONObject json, int index) {
    sendCloseMsg = "" + json;
    if (sendListener != null) {
      try {
        sendListener.accept(
            MockSendAdapter.class.getDeclaredMethod("sendClose", JSONObject.class, Integer.TYPE),
            sendCloseMsg);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void sendErrorClose(String request, int code, String message, int index) {
    JSONObject resJson = new JSONObject();
    try {
      resJson.put("code", code);

      if (message != null) {
        resJson.put("message", message);
      }
      String ret = resJson.toString();

      sendClose(ret, index);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void broadcast(String obj) {}

  @Override
  public <T extends User> void broadcast(String obj, Map<String, T> userlist) {}

  /**
   * Returns had sent messages.
   *
   * @return message
   */
  public String getSendMessgae() {
    return sendMsg;
  }

  /**
   * Returns had sent messages.
   *
   * @return message
   */
  public String getSendCloseMessgae() {
    return sendCloseMsg;
  }
}
