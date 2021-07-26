package ra.net.request;

/**
 * Connection request.
 *
 * @author Ray Li
 */
public class Request {
  private int index;
  private String ip;

  /** Initialize. */
  protected Request() {}

  /**
   * Initialize.
   *
   * @param request request
   */
  protected Request(Request request) {
    index = request.index;
    ip = request.ip;
  }

  /**
   * Returns index.
   *
   * @return index
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * Returns IP address of remote.
   *
   * @return address
   */
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

    /**
     * Set index.
     *
     * @param index index
     * @return Builder
     */
    public Builder setIndex(int index) {
      this.index = index;
      return this;
    }

    /**
     * Set IP address of remote.
     *
     * @param ip IP address
     * @return Builder
     */
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
