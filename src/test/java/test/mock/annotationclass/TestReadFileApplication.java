package test.mock.annotationclass;

import ra.net.nio.DataNetService;
import ra.net.processor.DataNetCommandProvider;

/**
 * Fake class.
 *
 * @author Ray Li
 */
public class TestReadFileApplication extends DataNetCommandProvider {
  @Override
  public void receivedRequest(DataNetService.DataNetRequest request) {
    request.getSender().send(request.getData());
  }
}
