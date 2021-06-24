package ra.net;

/**
 * 成員介面.
 *
 * @author Kevin Tasi
 */
public interface UserListener {

  /**
   * 成員索引(net索引).
   *
   * @return 索引值
   */
  public abstract int getIndex();

  /**
   * 指定索引值.
   *
   * @param index 成員索引值
   */
  public abstract void setIndex(int index);

  /**
   * 使用的語系，預設UTF-8.
   *
   * @return 語系字串
   */
  public abstract String getLangx();

  /**
   * 設定語系.
   *
   * @param langx 語系
   */
  public abstract void setLangx(String langx);

  /**
   * 取得成員的識別名稱.
   *
   * @return 成員的識別名稱
   */
  public abstract String getName();

  /**
   * 設定成員的識別名稱.
   *
   * @param username 成員的識別名稱
   */
  public abstract void setName(String username);

  /**
   * 取得成員的連線ip.
   *
   * @return 成員的連線ip
   */
  public abstract String getIp();

  /**
   * 設定成員的連線ip.
   *
   * @param ip 成員的連線ip
   */
  public abstract void setIp(String ip);

  /** 回收. */
  public abstract void close();
}
