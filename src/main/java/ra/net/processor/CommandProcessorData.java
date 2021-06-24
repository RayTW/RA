package ra.net.processor;

import ra.net.nio.Data;
import ra.net.request.DataRequest;
import ra.net.request.Request;

/**
 * Process data command.
 *
 * @author Ray Li
 */
public abstract class CommandProcessorData implements CommandProcessorListener<Data> {

  @Override
  public void commandProcess(Request<Data> request) {
    this.commandHandle(new DataRequest(request));
  }

  /**
   * Process data.
   *
   * @param request connection request
   */
  public abstract void commandHandle(DataRequest request);
}
