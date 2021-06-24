package ra.mock.server.basis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ra.net.MessageSender;
import ra.server.basis.Common;
import ra.server.basis.Global;
import ra.server.basis.MockSendAdapter;
import ra.server.basis.SenderAdapter;
import ra.util.Utility;

/** Test class. */
public class CommonTest {
  MockSendAdapter sendAdapter = new MockSendAdapter(new MessageSender());
  Common.Builder builder = new Common.Builder();
  Global global;

  @Before
  public void setUp() throws Exception {
    global = new Global();
  }

  /**
   * Test down.
   *
   * @throws Exception Exception
   */
  @After
  public void tearDown() throws Exception {
    global.getLogDelete().close();
  }

  @Test
  public void testCommonSendMonitor() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    int queueSize = 1;

    Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .build();

    for (int i = 0; i < queueSize; i++) {
      obj.getCountMachine().incrementAndGet();
    }

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    obj.sendMonitor(0, 1);

    JSONObject sendJson = new JSONObject(sendAdapter.getSendCloseMessgae()).getJSONObject("server");

    assertTrue(sendJson.has("name"));
    assertTrue(sendJson.has("alias"));
    assertTrue(sendJson.has("ip"));
    assertTrue(sendJson.has("port"));
    assertTrue(sendJson.has("queueSize"));
    assertTrue(sendJson.has("processCpu"));
    assertTrue(sendJson.has("processMemory"));

    assertEquals(sendJson.getString("name"), serverName);
    assertEquals(sendJson.getString("alias"), serverAlias);
    assertEquals(sendJson.getInt("port"), serverPort);
    assertEquals(sendJson.getInt("queueSize"), obj.getRequestCount());

    assertEquals(this.sendAdapter, obj.getSendData());
  }

  @Test
  public void testCommonSendMonitorWithAddition() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    int queueSize = 1;
    final Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .build();

    obj.setMonitorAdditionalInfo(
        () -> {
          return "test";
        });

    for (int i = 0; i < queueSize; i++) {
      obj.getCountMachine().incrementAndGet();
    }

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    obj.sendMonitor(0, 1);

    JSONObject sendJson = new JSONObject(sendAdapter.getSendCloseMessgae()).getJSONObject("server");

    assertTrue(sendJson.has("name"));
    assertTrue(sendJson.has("alias"));
    assertTrue(sendJson.has("ip"));
    assertTrue(sendJson.has("port"));
    assertTrue(sendJson.has("queueSize"));
    assertTrue(sendJson.has("processCpu"));
    assertTrue(sendJson.has("processMemory"));
    assertTrue(sendJson.has("additionalInfo"));

    assertEquals(sendJson.getString("name"), serverName);
    assertEquals(sendJson.getString("alias"), serverAlias);
    assertEquals(sendJson.getInt("port"), serverPort);
    assertEquals(sendJson.getInt("queueSize"), obj.getRequestCount());
    assertEquals(sendJson.getString("additionalInfo"), "test");

    assertEquals(this.sendAdapter, obj.getSendData());
  }

  @Test
  public void testCommonSendPongNeedInfo() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    String serverVersion = "a.b.c";
    final Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .setServerVersion(serverVersion)
            .build();

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    JSONObject request = new JSONObject();
    request.put("serverInfo", "true");
    obj.sendPong(request, 0);

    JSONObject response = new JSONObject();
    JSONObject info = new JSONObject();

    response.put("data", "pong");
    response.put("serverInfo", info);
    info.put("name", serverName);
    info.put("alias", serverAlias);
    info.put("version", serverVersion);

    assertEquals(response.toString(), sendAdapter.getSendCloseMessgae());
  }

  @Test
  public void testCommonSendPongInfoFlagNull() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    String serverVersion = "a.b.c";
    final Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .setServerVersion(serverVersion)
            .setHeartbeat("{\"data\":\"pong\"}")
            .build();

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    JSONObject request = new JSONObject();
    request.put("serverInfo", JSONObject.NULL);
    obj.sendPong(request, 0);

    JSONObject response = new JSONObject();

    response.put("data", "pong");

    assertEquals(response.toString(), sendAdapter.getSendCloseMessgae());
  }

  @Test
  public void testCommonSendPongNoInfo() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    final Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .setServerVersion("a.b.c")
            .setHeartbeat("{\"data\":\"pong\"}")
            .build();

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    obj.sendPong(new JSONObject(), 0);

    JSONObject response = new JSONObject();
    response.put("data", "pong");

    assertEquals(response.toString(), sendAdapter.getSendCloseMessgae());
  }

  @Test
  public void testCommonSendError() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    final Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .setServerVersion("a.b.c")
            .setHeartbeat("{\"data\":\"pong\"}")
            .build();

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    obj.sendError("request", 40000, "test", 0);

    JSONObject response = new JSONObject();
    response.put("data", "pong");

    assertEquals("{\"code\":40000,\"message\":\"test\"}", sendAdapter.getSendMessgae());
  }

  @Test
  public void testCommonSendErrorClose() {
    String serverName = "CommonService";
    String serverAlias = "CommonAlias";
    int serverPort = 1234;
    final Common obj =
        builder
            .setSenderAdapter(createSenderAdapter(global))
            .setServerName(serverName)
            .setServerAlias(serverAlias)
            .setServerPort(serverPort)
            .setServerVersion("a.b.c")
            .build();

    Utility.get().replaceMember(obj, "sendAdapter", sendAdapter);

    obj.sendErrorClose("request", 40000, "test", 0);

    JSONObject response = new JSONObject();
    response.put("data", "pong");

    assertEquals("{\"code\":40000,\"message\":\"test\"}", sendAdapter.getSendCloseMessgae());
  }

  private SenderAdapter createSenderAdapter(Global global) {
    return new SenderAdapter(new MessageSender(), global.getServerLog(), global.getErrorLog());
  }
}
