package ra.net;

import java.util.Map;

/**
 * 發送訊息的工具.
 *
 * @author Kevin Tasi
 */
public class MessageSender {
  private NetServiceProvider serviceProvider;

  public void setNetServiceProvider(NetServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  /**
   * 廣播至所有線上成員.
   *
   * @param obj 要廣播的訊息
   */
  public void boardcast(String obj) {
    boardcast(obj, this.serviceProvider.getUsers());
  }

  /**
   * 廣播至所有線上成員.
   *
   * @param obj 要廣播的訊息
   * @param userlist 成員列表
   */
  @SuppressWarnings("unchecked")
  public <T extends UserListener> void boardcast(String obj, Map<String, T> userlist) {
    userlist
        .entrySet()
        .parallelStream()
        .map(
            entry -> {
              Serviceable<String> net =
                  (Serviceable<String>)
                      this.serviceProvider.getService(entry.getValue().getIndex());

              return net;
            });
  }

  /**
   * 送出資料給成員.
   *
   * @param obj 要發送的訊息
   * @param index 成員的索引值
   */
  @SuppressWarnings("unchecked")
  public void send(String obj, int index) {
    Serviceable<String> net = (Serviceable<String>) this.serviceProvider.getService(index);

    try {
      net.send(obj);
    } catch (Exception e) {
      e.printStackTrace();
      net.onClose();
    }
  }

  /**
   * 送完資料給成員後關閉連線.
   *
   * @param obj 要發送的訊息
   * @param index 成員的索引值
   */
  @SuppressWarnings("unchecked")
  public void sendClose(String obj, int index) {
    Serviceable<String> net = (Serviceable<String>) this.serviceProvider.getService(index);

    try {
      net.sendClose(obj);
    } catch (Exception e) {
      net.onClose();
      e.printStackTrace();
    }
  }
}
