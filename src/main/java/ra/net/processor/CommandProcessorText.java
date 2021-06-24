package ra.net.processor;

import ra.net.request.Request;
import ra.net.request.TextRequest;

/**
 * Process text command.
 *
 * @author Ray Li
 */
public abstract class CommandProcessorText implements CommandProcessorListener<String> {

  @Override
  public void commandProcess(Request<String> request) {
    this.commandHandle(new TextRequest(request));
  }

  /**
   * Process data.
   *
   * @param request connection request
   */
  public abstract void commandHandle(TextRequest request);
}
