package ra.exception;

/**
 * Exception.
 *
 * @author Ray Li.
 */
public class RaSqlException extends RuntimeException {
  private static final long serialVersionUID = 8060107933627536970L;

  public RaSqlException() {
    super();
  }

  /**
   * Initialize.
   *
   * @param cause cause
   */
  public RaSqlException(Throwable cause) {
    super(cause);
  }

  public RaSqlException(String message) {
    super(message);
  }

  /**
   * Initialize.
   *
   * @param message message
   * @param cause cause
   */
  public RaSqlException(String message, Throwable cause) {
    super(
        message == null || message.length() == 0
            ? cause.toString()
            : cause.toString() + " " + message,
        cause);
  }
}
