package test.mock;

import ra.net.NetService;
import ra.net.processor.NetCommandProvider;

/**
 * Mock class.
 *
 * @author Ray Li
 */
public class MockNetServiceCommand extends NetCommandProvider {

  public void receivedRequest(NetService.NetRequest request) {
    System.out.println("text[" + request.getText() + "],request[" + request + "]");
  }
}
