package test.mock.annotationclass;

import ra.net.processor.DataNetServiceCommandProvider;
import ra.net.request.DataRequest;

/**
 * Fake class.
 *
 * @author Ray Li
 */
public class TestReadZipCommand extends DataNetServiceCommandProvider {
  @Override
  public void receivedRequest(DataRequest request) {
    request.getSender().send(request.getData());
  }
}
