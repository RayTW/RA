package test.mock.annotationclass;

import ra.net.processor.DataNetCommandProvider;
import ra.net.request.DataRequest;

/**
 * Fake class.
 *
 * @author Ray Li
 */
public class TestReadZipCommand extends DataNetCommandProvider {
  @Override
  public void receivedRequest(DataRequest request) {
    request.getSender().send(request.getData());
  }
}
