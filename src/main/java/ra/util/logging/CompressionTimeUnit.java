package ra.util.logging;

/**
 * 產生壓縮檔的檔名時間單位.
 *
 * @author Ray Li
 */
public enum CompressionTimeUnit {
  /** 每天產生新檔名. */
  DAY("yyyy-MM-dd"),
  /** 每小時產生新檔名. */
  HOUR("yyyy-MM-dd_hh"),
  /** 每分鐘產生新檔名. */
  MINUTE("yyyy-MM-dd_hhmm");

  private final String dateFormatPattern;

  private CompressionTimeUnit(String pattern) {
    dateFormatPattern = pattern;
  }

  /**
   * 日誌檔名格式.
   *
   * @return date format pattern
   */
  public String getPattern() {
    return dateFormatPattern;
  }
}
