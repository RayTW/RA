package ra.server.basis;

import ra.net.request.DefaultRequest;

/**
 * Service.
 *
 * @author Ray Li
 * @param <T> type
 */
public interface ServiceLite<T> {
  /**
   * Receive request.
   *
   * @param request request
   * @param response response
   * @param obj obj
   * @param common common
   */
  void doJob(DefaultRequest request, Response response, T obj, Common common);
}
