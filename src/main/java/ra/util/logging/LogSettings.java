package ra.util.logging;

import ra.util.compression.CompressionMode;

/**
 * 日誌設定.
 *
 * @author Ray Li
 */
public class LogSettings {
  private boolean enable;
  private String path;
  private int keepDays;
  private CompressionTimeUnit timeUnit = CompressionTimeUnit.DAY;
  private CompressionMode mode = CompressionMode.LOG;

  public LogSettings() {}

  public boolean getEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getKeepDays() {
    return keepDays;
  }

  public void setKeepDays(int keepdays) {
    keepDays = keepdays;
  }

  /**
   * 儲存日誌進行即時壓縮設定 預設啟用CompressionTimeUnit.DAY.
   *
   * @param mode 日誌檔案格式
   */
  public void setCompressionMode(CompressionMode mode) {
    this.mode = mode;
  }

  /**
   * 儲存產生日誌檔的時間單位、是否對日誌進行即時壓縮.
   *
   * @param timeUnit 產生日誌檔的時間單位
   */
  public void setCompressionTimeUnit(CompressionTimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  public CompressionTimeUnit getCompressionTimeUnit() {
    return timeUnit;
  }

  /** 取得日誌檔案存檔格式. */
  public CompressionMode getCompressionMode() {
    return mode;
  }
}
