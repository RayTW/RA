package ra.net.processor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ra.net.nio.Data;
import ra.net.nio.DataNetService;

/** Test class. */
public class DataNetCommandProviderTest {

  @Test
  public void testReceivedRequest() {
    class DataNet extends DataNetCommandProvider {
      private boolean isReceivedRequest = false;

      @Override
      public void receivedRequest(DataNetService.DataNetRequest request) {
        super.receivedRequest(request);
        isReceivedRequest = true;
      }
    }

    DataNetService.DataNetRequest request =
        new DataNetService.DataNetRequest.Builder().setData(new Data("test")).build();

    DataNet obj = new DataNet();

    obj.receivedRequest(request);

    assertTrue(obj.isReceivedRequest);
  }
}
