package ra.server.basis;

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import ra.net.MessageSender;

/** Test class. */
public class ResponseTest {
  MockSendAdapter sendAdapter = new MockSendAdapter(new MessageSender());

  @Test
  public void testCreateResponse() {
    Response response = new Response(1, sendAdapter);

    assertEquals(response.getIndex(), 1);
  }

  @Test
  public void testSendString() {
    Response response = new Response(1, sendAdapter);

    response.send("test");

    assertEquals(sendAdapter.getSendMessgae(), "test");
  }

  @Test
  public void testSendJson() {
    Response response = new Response(1, sendAdapter);

    response.send(new JSONObject());

    assertEquals(sendAdapter.getSendMessgae(), "{}");
  }

  @Test
  public void testSendJsonArray() {
    Response response = new Response(1, sendAdapter);

    response.send(new JSONArray());

    assertEquals(sendAdapter.getSendMessgae(), "[]");
  }

  @Test
  public void testSendCloseString() {
    Response response = new Response(1, sendAdapter);

    response.sendClose("test");

    assertEquals(sendAdapter.getSendCloseMessgae(), "test");
  }

  @Test
  public void testSendCloseJson() {
    Response response = new Response(1, sendAdapter);

    response.sendClose(new JSONObject());

    assertEquals(sendAdapter.getSendCloseMessgae(), "{}");
  }

  @Test
  public void testSendCloseJsonArray() {
    Response response = new Response(1, sendAdapter);

    response.sendClose(new JSONArray());

    assertEquals(sendAdapter.getSendCloseMessgae(), "[]");
  }

  @Test
  public void testSendError() {
    Response response = new Response(1, sendAdapter);

    response.sendError("request", 100, "test error");

    assertEquals(sendAdapter.getSendMessgae(), "{\"code\":100,\"message\":\"test error\"}");
  }

  @Test
  public void testSendErrorClose() {
    Response response = new Response(1, sendAdapter);

    response.sendErrorClose("request", 100, "test error");

    assertEquals(sendAdapter.getSendCloseMessgae(), "{\"code\":100,\"message\":\"test error\"}");
  }
}
