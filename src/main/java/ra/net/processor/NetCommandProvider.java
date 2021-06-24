package ra.net.processor;

import ra.net.request.TextRequest;

/**
 * Provides {@link String} command processor.
 *
 * @author Ray Li
 */
public class NetCommandProvider implements CommandProcessorProvider<String> {

  @Override
  public CommandProcessorListener<String> createCommand() {
    return new CommandProcessorText() {

      @Override
      public void commandHandle(TextRequest request) {
        receivedRequest(request);
      }
    };
  }

  public void receivedRequest(TextRequest request) {
    System.out.println("text[" + request.getText() + "],request[" + request + "]");
  }

  @Override
  public void offline(int index) {}
}
