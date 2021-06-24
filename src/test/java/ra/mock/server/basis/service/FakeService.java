package ra.mock.server.basis.service;

import ra.net.request.DefaultRequest;
import ra.server.basis.Common;
import ra.server.basis.Response;
import ra.server.basis.ServiceLite;
import ra.util.annotation.RequestCommand;

/** Test class. */
@RequestCommand("/fake/test")
public class FakeService implements ServiceLite<Object> {

  @Override
  public void doJob(DefaultRequest request, Response response, Object obj, Common common) {}
}
