package ra.server.basis;

import ra.net.request.DefaultRequest;

/**
 * Service.
 *
 * @author Ray Li
 * @param <T> type
 */
public interface ServiceLite<T> {
  void doJob(DefaultRequest request, Response response, T obj, Common common);
}
