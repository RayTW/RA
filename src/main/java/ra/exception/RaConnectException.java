package ra.exception;

/**
 * Exception.
 *
 * @author Ray Li.
 */
public class RaConnectException extends RuntimeException {
  private static final long serialVersionUID = 2444597956732110772L;

  public RaConnectException() {
    super();
  }

  public RaConnectException(Throwable cause) {
    super(cause);
  }

  public RaConnectException(String message) {
    super(message);
  }

  /**
   * Initialize.
   *
   * @param message message
   * @param cause cause
   */
  public RaConnectException(String message, Throwable cause) {
    super(
        message == null || message.length() == 0
            ? cause.toString()
            : cause.toString() + " " + message,
        cause);
  }
}
