package ra.net.processor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ra.net.NetService;

/** Test class. */
public class NetCommandProviderTest {

  @Test
  public void testReceivedRequest() {
    class Net extends NetCommandProvider {
      private boolean isReceivedRequest = false;

      @Override
  public void receivedRequest(NetService.NetRequest request) {
        super.receivedRequest(request);
        isReceivedRequest = true;
      }
    }

    NetService.NetRequest request = new NetService.NetRequest.Builder().setText("text").build();

    Net obj = new Net();

    obj.receivedRequest(request);

    assertTrue(obj.isReceivedRequest);
  }
}
