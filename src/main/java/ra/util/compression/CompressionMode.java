package ra.util.compression;

/**
 * 日誌檔案支援的壓縮模式.
 *
 * @author Ray Li
 */
public enum CompressionMode {
  /** 一般文字檔. */
  LOG("log"),
  /** GZIP壓縮. */
  GZIP("gz"),
  /** SNAPPY壓縮. */
  SNAPPY("snappy");

  private final String filenameExtension;

  private CompressionMode(String filenameExtension) {
    this.filenameExtension = filenameExtension;
  }

  /** 日誌檔案壓縮副檔名. */
  public String getFilenameExtension() {
    return filenameExtension;
  }
}
