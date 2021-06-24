package ra.net.request;

/**
 * Text request.
 *
 * @author Ray Li
 */
public class TextRequest extends Request<String> {
  private String text;

  /**
   * Initialize.
   *
   * @param request request
   */
  public TextRequest(Request<String> request) {
    super(request);
    text = new String(request.getDataBytes());
  }

  public String getText() {
    return text;
  }
}
