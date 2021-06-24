package ra.net.request;

import ra.net.Sendable;

/**
 * Connection request.
 *
 * @author Ray Li
 */
public class Request<T> {
  private String ip;
  private int index;
  private byte[] bytes;
  private Sendable<T> sender;

  public Request() {}

  public Request(int index) {
    this.index = index;
  }

  /**
   * Initialize.
   *
   * @param request request
   */
  public Request(Request<T> request) {
    this.index = request.index;
    this.sender = request.sender;
    this.ip = request.ip;
    this.bytes = request.bytes;
  }

  public void setSender(Sendable<T> sender) {
    this.sender = sender;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getIp() {
    return ip;
  }

  public void setDataBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  protected byte[] getDataBytes() {
    return bytes;
  }

  public int getIndex() {
    return index;
  }

  public Sendable<T> getSender() {
    return this.sender;
  }

  @Override
  public String toString() {
    return String.format("ip=%s, index=%d, bytes=%s\n", ip, index, bytes);
  }
}
