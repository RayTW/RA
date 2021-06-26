package ra.util.compression;

/**
 * Compression mode supported by log files.
 *
 * @author Ray Li
 */
public enum CompressionMode {
  /** text. */
  LOG("log"),
  /** GZIP. */
  GZIP("gz"),
  /** SNAPPY. */
  SNAPPY("snappy");

  private final String filenameExtension;

  private CompressionMode(String filenameExtension) {
    this.filenameExtension = filenameExtension;
  }

  /**
   * Returns extension name of file.
   *
   * @return file extension name
   */
  public String getFilenameExtension() {
    return filenameExtension;
  }
}
