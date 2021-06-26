package ra.net;

/**
 * Abstraction layer for user.
 *
 * @author Ray Li,Kevin Tasi
 */
public interface UserListener {

  /**
   * Index of member in user pool.
   *
   * @return index
   */
  public abstract int getIndex();

  /**
   * Assign index for the user.
   *
   * @param index index
   */
  public abstract void setIndex(int index);

  /**
   * Gets name of user.
   *
   * @return user name
   */
  public abstract String getName();

  /**
   * Set name for the user.
   *
   * @param username 成員的識別名稱
   */
  public abstract void setName(String username);

  /**
   * Gets IP address of the user.
   *
   * @return IP address
   */
  public abstract String getIp();

  /**
   * Set the IP address for the user.
   *
   * @param ip IP address
   */
  public abstract void setIp(String ip);

  /** Close. */
  public abstract void close();
}
