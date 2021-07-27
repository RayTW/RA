package ra.net.processor;

import ra.net.NetService;

/**
 * Provides {@link String} command processor.
 *
 * @author Ray Li
 */
public class NetCommandProvider implements CommandProcessorProvider<NetService.NetRequest> {

  @Override
  public CommandProcessorListener<NetService.NetRequest> createCommand() {
    return new NetCommandProcessor() {

      @Override
      public void commandProcess(NetService.NetRequest request) {
        receivedRequest(request);
      }
    };
  }

  /**
   * Process event that received the request.
   *
   * @param request request
   */
  public void receivedRequest(NetService.NetRequest request) {
    System.out.println(
        "class[" + this.getClass() + "],text[" + request.getText() + "],request[" + request + "]");
  }

  @Override
  public void offline(int index) {}
}
