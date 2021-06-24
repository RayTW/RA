package ra.net.processor;

import ra.net.nio.Data;
import ra.net.request.DataRequest;

/**
 * Provides {@link Data} command processor.
 *
 * @author Ray Li
 */
public class DataNetCommandProvider implements CommandProcessorProvider<Data> {

  @Override
  public CommandProcessorListener<Data> createCommand() {
    return new CommandProcessorData() {

      @Override
      public void commandHandle(DataRequest request) {
        receivedRequest(request);
      }
    };
  }

  public void receivedRequest(DataRequest request) {
    System.out.println("data[" + request.getData() + "],request[" + request + "]");
  }

  @Override
  public void offline(int index) {}
}
