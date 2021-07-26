package ra.server.basis;

/**
 * When server with client transmission abnormal will throw {@link CommandNotFoundException}.
 *
 * @author Ray Li
 */
public class CommandNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Initialize.
   *
   * @param message message
   */
  public CommandNotFoundException(String message) {
    super(message);
  }
}
