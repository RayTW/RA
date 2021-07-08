package ra.net.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;
import ra.net.NetService;
import ra.net.Sendable;
import ra.ref.BooleanReference;

/** Test class. */
public class DefaultRequestTest {

  @Test
  public void testCreateEmptyRequest() {
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();

    builder.setIndex(1);
    JSONObject json = new JSONObject();
    builder.setText(json.toString());

    DefaultRequest request = new DefaultRequest(builder.build());

    assertEquals(1, request.getIndex());
  }

  @Test
  public void testCreateRequest() {
    JSONObject json = new JSONObject();

    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();

    builder.setIndex(1);
    builder.setIp("1.2.3.4");
    json.put("key", "abc");
    builder.setText(json.toString());

    BooleanReference sendResult = new BooleanReference(false);
    BooleanReference sendCloseResult = new BooleanReference(false);
    builder.setSender(
        new Sendable<String>() {

          @Override
          public void send(String message) {
            sendResult.set(true);
          }

          @Override
          public void sendClose(String message) {
            sendCloseResult.set(true);
          }
        });

    DefaultRequest request = new DefaultRequest(builder.build());

    request.send("xx");
    request.sendClose("bb");

    assertEquals(1, request.getIndex());
    assertEquals("{\"key\":\"abc\"}", request.getSource());
    assertEquals(1, request.getIndex());
    assertEquals("1.2.3.4", request.getIp());
    assertTrue(sendResult.get());
    assertTrue(sendCloseResult.get());
  }

  @Test
  public void testInitializeCommand() {
    String expected = "hello command";
    JSONObject json = new JSONObject();
    json.put("command", expected);

    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText(json.toString());
    DefaultRequest request = new DefaultRequest(builder.build());

    assertEquals(expected, request.getCommand());
  }

  @Test
  public void testInitializeAuthorization() {
    String expected = "authorization123";
    JSONObject json = new JSONObject();
    json.put("authorization", expected);

    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText(json.toString());
    DefaultRequest request = new DefaultRequest(builder.build());

    assertEquals(expected, request.getAuthorization());
  }

  @Test
  public void testCommand() {
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText("{}");
    DefaultRequest request = new DefaultRequest(builder.build());
    String expected = "hello command";
    request.setCommand(expected);

    assertEquals(expected, request.getCommand());
  }

  @Test
  public void testAuthorization() {
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText("{}");
    DefaultRequest request = new DefaultRequest(builder.build());
    String expected = "Authorization";
    request.setAuthorization(expected);

    assertEquals(expected, request.getAuthorization());
  }

  @Test
  public void testJson() {
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText("{}");
    DefaultRequest request = new DefaultRequest(builder.build());
    JSONObject json = new JSONObject();
    json.put("test", false);
    request.setJson(json);

    assertSame(json, request.getJson());
  }

  @Test
  public void testSource() {
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText("{}");
    DefaultRequest request = new DefaultRequest(builder.build());
    String expected = "source";
    request.setSource(expected);

    assertSame(expected, request.getSource());
  }

  @Test
  public void testReciveTime() {
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    builder.setIndex(0);
    builder.setText("{}");
    DefaultRequest request = new DefaultRequest(builder.build());
    long expected = 111;
    request.setReciveTime(expected);

    assertEquals(expected, request.getReciveTime());
  }
}
