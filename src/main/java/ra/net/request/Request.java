package ra.net.request;

/**
 * Connection request.
 *
 * @author Ray Li
 */
public class Request {
  private int index;
  private String ip;

  protected Request() {}

  protected Request(Request request) {
    index = request.index;
    ip = request.ip;
  }

  public int getIndex() {
    return this.index;
  }

  public String getIp() {
    return this.ip;
  }

  /**
   * builder.
   *
   * @author Ray Li
   */
  public static class Builder {
    private int index;
    private String ip;

    public Builder setIndex(int index) {
      this.index = index;
      return this;
    }

    public Builder setIp(String ip) {
      this.ip = ip;
      return this;
    }

    /**
     * build.
     *
     * @return Request
     */
    public Request build() {
      Request obj = new Request();

      obj.index = this.index;
      obj.ip = this.ip;

      return obj;
    }
  }
}
