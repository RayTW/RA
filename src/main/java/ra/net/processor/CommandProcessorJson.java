package ra.net.processor;

import ra.net.request.DefaultRequest;
import ra.net.request.Request;

/**
 * Process JSON command.
 *
 * @author Ray Li
 */
public abstract class CommandProcessorJson implements CommandProcessorListener<String> {

  @Override
  public void commandProcess(Request<String> request) {
    DefaultRequest defaultRequest = new DefaultRequest(request);

    commandHandle(defaultRequest);
  }

  /**
   * Process data.
   *
   * @param request connection request
   */
  public abstract void commandHandle(DefaultRequest request);
}
