package ra.net;

import java.util.Map;

/**
 * Provide {@link NetService}.
 *
 * @author Ray Li, Kevin Tasi
 */
public interface NetServiceProvider {

  /**
   * Hash Key : Net{0~n}.
   *
   * @param index index
   * @return 儲存BlanceNetthreadKByte的容器
   */
  public abstract Serviceable<?> getService(int index);

  /**
   * Hash Key : {User.getIndex()} ex:0~n.
   *
   * @return 儲存User的容器
   */
  public abstract Map<String, UserListener> getUsers();
}
