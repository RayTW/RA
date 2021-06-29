package ra.util.logging;

/**
 * The time unit of the file name in creates a compressed file.
 *
 * @author Ray Li
 */
public enum CompressionTimeUnit {
  /** Create new file per every day. */
  DAY("yyyy-MM-dd"),
  /** Create new file per every hour. */
  HOUR("yyyy-MM-dd_hh"),
  /** Create new file per every minute. */
  MINUTE("yyyy-MM-dd_hhmm");

  private final String dateFormatPattern;

  private CompressionTimeUnit(String pattern) {
    dateFormatPattern = pattern;
  }

  /**
   * get the date format pattern.
   *
   * @return date format pattern
   */
  public String getPattern() {
    return dateFormatPattern;
  }
}
