package ra.db.parameter;

/**
 * Accountable.
 *
 * @author ray_lee
 */
public interface Accountable {
  /**
   * Returns database user.
   *
   * @return user
   */
  public String getUser();

  /**
   * Returns database password.
   *
   * @return password
   */
  public String getPassword();
}
