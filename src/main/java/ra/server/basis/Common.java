package ra.server.basis;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.json.JSONException;
import org.json.JSONObject;
import ra.util.Monitor;

/**
 * Common class.
 *
 * @author Ray Li
 */
public final class Common {
  private Monitor monitor;
  private AtomicInteger countMachine;
  private SenderAdapter sendAdapter;
  private String serverInfo;
  private String serverName;
  private String serverAlias;
  private int serverPort;
  private String heartbeat;
  private Optional<Supplier<String>> additionalInfo;

  private Common() {
    additionalInfo = Optional.ofNullable(null);
  }

  /**
   * Set the monitoring value to return additional additional information.
   *
   * @param info additional information
   */
  public void setMonitorAdditionalInfo(Supplier<String> info) {
    additionalInfo = Optional.ofNullable(info);
  }

  /**
   * Send error message.
   *
   * @param request request
   * @param code error code
   * @param message message
   * @param index index
   */
  public void sendError(String request, int code, String message, int index) {
    sendAdapter.sendError(request, code, message, index);
  }

  /**
   * Logging after sending error message and close connection.
   *
   * @param request request
   * @param code error code
   * @param message message
   * @param index index
   */
  public void sendErrorClose(String request, int code, String message, int index) {
    sendAdapter.sendErrorClose(request, code, message, index);
  }

  /**
   * Send heartbeat.
   *
   * @param json json
   * @param index index
   */
  public void sendPong(JSONObject json, int index) {
    boolean serverInfo = false;

    if (!json.has("serverInfo") || json.isNull("serverInfo")) {
      serverInfo = false;
    } else {
      serverInfo = json.optBoolean("serverInfo");
    }

    if (serverInfo) {
      sendAdapter.sendClose(this.serverInfo, index);
    } else {
      sendAdapter.sendClose(this.heartbeat, index);
    }
  }

  /**
   * Send server status.
   *
   * @param index index
   * @param queueSize queueSize
   */
  public void sendMonitor(int index, long queueSize) {
    JSONObject obj = new JSONObject();
    JSONObject server = new JSONObject();

    try {
      server.put("name", serverName);
      server.put("alias", serverAlias);
      server.put("ip", monitor.optLocalHostAddress(JSONObject.NULL));
      server.put("port", serverPort);
      server.put("queueSize", queueSize);
      server.put("processCpu", monitor.getProcessCpuLoad());
      server.put("processMemory", monitor.getProcessMemoryLoad());

      additionalInfo.ifPresent(v -> server.put("additionalInfo", v.get()));

      obj.put("server", server);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    sendAdapter.sendClose(obj.toString(), index);
  }

  /**
   * Returns request counts that current processing.
   *
   * @return request count
   */
  public long getRequestCount() {
    return countMachine.get();
  }

  /**
   * Returns response sender.
   *
   * @return sender
   */
  public SenderAdapter getSendData() {
    return sendAdapter;
  }

  /**
   * Returns machine counter.
   *
   * @return counter
   */
  public AtomicInteger getCountMachine() {
    return countMachine;
  }

  /**
   * Returns host address that current server.
   *
   * @return host address
   */
  public String getServerIp() {
    return "" + monitor.optLocalHostAddress(JSONObject.NULL);
  }

  /** Builder. */
  public static class Builder {
    private String serverName;
    private String serverAlias;
    private int serverPort;
    private String serverVersion;
    private String heartbeat;
    private SenderAdapter sender;

    /**
     * Set sender.
     *
     * @param sender sender
     * @return Builder
     */
    public Builder setSenderAdapter(SenderAdapter sender) {
      this.sender = sender;
      return this;
    }

    /**
     * Set server name.
     *
     * @param name server name
     * @return Builder
     */
    public Builder setServerName(String name) {
      serverName = name;
      return this;
    }

    /**
     * Set server alias.
     *
     * @param alias server alias
     * @return Builder
     */
    public Builder setServerAlias(String alias) {
      serverAlias = alias;
      return this;
    }

    /**
     * Set server port.
     *
     * @param port server port
     * @return Builder
     */
    public Builder setServerPort(int port) {
      serverPort = port;
      return this;
    }

    /**
     * Set server version.
     *
     * @param version server version
     * @return Builder
     */
    public Builder setServerVersion(String version) {
      serverVersion = version;
      return this;
    }

    /**
     * Set server heart beat.
     *
     * @param heartbeat heart beat
     * @return Builder
     */
    public Builder setHeartbeat(String heartbeat) {
      this.heartbeat = heartbeat;
      return this;
    }

    /**
     * Build Common.
     *
     * @return Common
     */
    public Common build() {
      Common common = new Common();

      common.monitor = new Monitor();
      common.sendAdapter = sender;
      common.countMachine = new AtomicInteger();
      common.serverPort = serverPort;
      common.serverName = serverName;
      common.serverAlias = serverAlias;

      JSONObject pong = new JSONObject();
      JSONObject info = new JSONObject();

      pong.put("data", "pong");
      pong.put("serverInfo", info);
      info.put("name", serverName);
      info.put("alias", serverAlias);
      info.put("version", serverVersion);

      common.serverInfo = pong.toString();
      common.heartbeat = heartbeat;
      return common;
    }
  }
}
