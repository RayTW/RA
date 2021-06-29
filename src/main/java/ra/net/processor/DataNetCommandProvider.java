package ra.net.processor;

import ra.net.nio.Data;
import ra.net.nio.DataNetService;

/**
 * Provides {@link Data} command processor.
 *
 * @author Ray Li
 */
public class DataNetCommandProvider
    implements CommandProcessorProvider<DataNetService.NetDataRequest> {

  @Override
  public CommandProcessorListener<DataNetService.NetDataRequest> createCommand() {
    return new DataNetCommandProcessor() {

      @Override
      public void commandProcess(DataNetService.NetDataRequest request) {
        receivedRequest(request);
      }
    };
  }

  public void receivedRequest(DataNetService.NetDataRequest request) {
    System.out.println("data[" + request.getData() + "],request[" + request + "]");
  }

  @Override
  public void offline(int index) {}
}
