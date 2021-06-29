package ra.util.logging;

import ra.util.compression.CompressionMode;

/**
 * General Setting parameters for Log.
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
   * The Compression Mode on a real-time saves Log. Default： CompressionMode.LOG
   *
   * @param mode Log Compression Mode
   */
  public void setCompressionMode(CompressionMode mode) {
    this.mode = mode;
  }

  /**
   * The time unit of the Log file be created. Default： CompressionTimeUnit.DAY
   *
   * @param timeUnit The time unit of the Log file be created
   */
  public void setCompressionTimeUnit(CompressionTimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  public CompressionTimeUnit getCompressionTimeUnit() {
    return timeUnit;
  }

  /**
   * Get the Compression Mode of saving Log file.
   *
   * @return {@link CompressionMode}
   */
  public CompressionMode getCompressionMode() {
    return mode;
  }
}
