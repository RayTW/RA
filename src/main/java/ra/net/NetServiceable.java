package ra.net;

/**
 * 通訊中間層處理.
 *
 * @author Ray Li
 */
public interface NetServiceable extends Serviceable<String> {
  /** 取得是否加密. */
  public abstract boolean getSendcompilete();

  /**
   * 設定是否需要傳送加密.
   *
   * @param compilete 是否加密
   */
  public abstract void setSendcompilete(boolean compilete);
}
