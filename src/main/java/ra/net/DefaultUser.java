package ra.net;

/** User. */
public class DefaultUser implements User {
  private int index = -1;
  private String ip;
  private String name = "";

  /**
   * Returns index of member in user pool.
   *
   * @return index
   */
  @Override
  public int getIndex() {
    return this.index;
  }

  /**
   * Assign index for the user.
   *
   * @param index index
   */
  @Override
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * Returns name of user.
   *
   * @return user name
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Set name for the user.
   *
   * @param username username
   */
  @Override
  public void setName(String username) {
    this.name = username;
  }

  /**
   * Returns IP address of the user.
   *
   * @return IP address
   */
  @Override
  public String getIp() {
    return this.ip;
  }

  /**
   * Set the IP address for the user.
   *
   * @param ip IP address
   */
  @Override
  public void setIp(String ip) {
    this.ip = ip;
  }

  /** Close. */
  @Override
  public void close() {
    ip = null;
    name = null;
  }
}
