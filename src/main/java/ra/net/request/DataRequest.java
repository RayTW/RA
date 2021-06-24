package ra.net.request;

import ra.net.nio.Data;
import ra.net.nio.DataType;

/**
 * {@link Data} request.
 *
 * @author Ray Li
 */
public class DataRequest extends Request<Data> {
  private Data data;

  /**
   * Initialize.
   *
   * @param request request
   */
  public DataRequest(Request<Data> request) {
    super(request);
    byte[] raw = request.getDataBytes();
    DataType type = DataType.valueOf(raw);
    byte[] bytes = new byte[raw.length - 2];
    System.arraycopy(raw, 2, bytes, 0, bytes.length);

    data = new Data(type, bytes);
  }

  public Data getData() {
    return data;
  }
}
