package ra.net.processor;

import ra.net.nio.Data;
import ra.net.nio.DataNetService;

/**
 * Provides {@link Data} command processor.
 *
 * @author Ray Li
 */
public class DataNetCommandProvider
    implements CommandProcessorProvider<DataNetService.DataNetRequest> {

  @Override
  public CommandProcessorListener<DataNetService.DataNetRequest> createCommand() {
    return new DataNetCommandProcessor() {

      @Override
      public void commandProcess(DataNetService.DataNetRequest request) {
        receivedRequest(request);
      }
    };
  }

  /**
   * Process event that received the request.
   *
   * @param request request
   */
  public void receivedRequest(DataNetService.DataNetRequest request) {
    System.out.println(
        "class[" + this.getClass() + "],data[" + request.getData() + "],request[" + request + "]");
  }

  @Override
  public void offline(int index) {}
}
