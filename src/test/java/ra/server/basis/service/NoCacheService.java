package ra.server.basis.service;

import ra.net.request.DefaultRequest;
import ra.server.basis.Common;
import ra.server.basis.Response;
import ra.server.basis.ServiceLite;
import ra.util.annotation.RequestCommand;

/** Test class. */
@RequestCommand(value = "/service/no-cache", cache = false)
public class NoCacheService implements ServiceLite<Object> {

  @Override
  public void doJob(DefaultRequest request, Response response, Object obj, Common common) {}
}
