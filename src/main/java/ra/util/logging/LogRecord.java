package ra.util.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ra.util.SpaceUnit;
import ra.util.Utility;
import ra.util.compression.CompressionMode;
import ra.util.compression.GzipFileStringOutput;
import ra.util.compression.SnappyFileStringOutput;
import ra.util.compression.StringFileOutput;
import ra.util.compression.StringOutput;

/**
 * Create a file which be used to write Log. the following action: Keep the file I/O connection and
 * will not close the file I/O connection when writing log into a file, after creating the Log file
 * by earmark file name.
 *
 * @author Ray Li
 */
public class LogRecord {
  private File logFileName;
  private File logFileFolder;
  private StringOutput stringOutput;
  private boolean dateFormatFlag;
  private CompressionMode compressionMode;
  private SimpleDateFormat simpleDateFormat;
  private long maxFileSize; // The Limit size of Log file.
  private OnNotEnoughSpaceListener onNotEnoughSpaceListener;

  /** Initialize. */
  public LogRecord() {
    dateFormatFlag = true;
    compressionMode = CompressionMode.LOG;
    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  }

  public void setOnNotEnoughSpaceListener(OnNotEnoughSpaceListener listener) {
    onNotEnoughSpaceListener = listener;
  }

  /**
   * The Date format style of write log into Log file.
   *
   * @param format The Date format style
   */
  public void setSimpleDateFormat(String format) {
    simpleDateFormat = new SimpleDateFormat(format);
  }

  /** Enable add the head(Date-time information) when write log into Log file. */
  public void enableDateFormat() {
    dateFormatFlag = true;
  }

  /** Disenable add the head (Date-time information) when write log into Log file. */
  public void disenableDateFormat() {
    dateFormatFlag = false;
  }

  /**
   * Setting the Compression Mode on a real-time saves Log.
   *
   * @param mode the file Compression Mode
   */
  public void setCompressionMode(CompressionMode mode) {
    compressionMode = mode;
  }

  /**
   * initialize.
   *
   * @param folderPath the Folder path of the Log
   * @param fileName the file name of Log
   * @param charset The Character encoding of Log file
   * @return initialize state
   */
  public boolean init(String folderPath, String fileName, String charset) {
    logFileFolder = new File(folderPath);
    logFileFolder.mkdirs();
    logFileName = new File(folderPath, fileName);

    try {
      logFileName.createNewFile();

      switch (compressionMode) {
        case GZIP:
          stringOutput = new GzipFileStringOutput(logFileName, charset);
          break;
        case SNAPPY:
          stringOutput = new SnappyFileStringOutput(logFileName, charset);
          break;
        default:
          stringOutput = new StringFileOutput(logFileName, charset);
      }

      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  /** Close the Log file. */
  public void close() {
    try {
      stringOutput.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Will not have text wraps automatically when write the log into file.
   *
   * @param str message of the log record.
   */
  public void write(String str) {
    // Stop writing to Log file, when has not enough space.
    if (!hasRemaining()) {
      if (onNotEnoughSpaceListener != null) {
        onNotEnoughSpaceListener.onNotEnoughSpace(logFileFolder);
      }
      return;
    }

    if (dateFormatFlag) {
      str = simpleDateFormat.format(new Date()) + " " + str;
    }

    if (!str.isEmpty()) {
      try {
        stringOutput.write(str);
        stringOutput.flush();
      } catch (IOException e) {
        // Maybe has the deadlock, if use e.printStackTrace() when has not enough space.
        System.out.println(Utility.get().getThrowableDetail(e));
      }
    }
  }

  /**
   * Will have text wraps automatically when write the log into file.
   *
   * @param str log
   */
  public void writeln(String str) {
    write(str + System.lineSeparator());
  }

  /**
   * Get the file name of the Log file.
   *
   * @return file name of the Log file
   */
  public File getLogFile() {
    return logFileName;
  }

  public File getLogFileFolder() {
    return logFileFolder;
  }

  /**
   * Setting the limit size of Log file.
   *
   * @param space numbers in unit
   * @param unit the unit of space
   */
  public void setMaxFileSize(long space, SpaceUnit unit) {
    maxFileSize = SpaceUnit.Bytes.convert(space, unit);
  }

  /**
   * Can it write in, after checking the storage space remaining?.
   * Default：Infinity
   *
   * @return Returns true when storage space remaining.
   */
  public boolean hasRemaining() {
    if (maxFileSize == 0L || logFileName.length() < maxFileSize) {
      return true;
    }

    return false;
  }

  /** It will be called on, when has not enough storage space remaining. */
  public static interface OnNotEnoughSpaceListener {
    /**
     * It will be called on, when has not enough storage space remaining.
     *
     * @param logRootFolder the Folder path of the Log
     */
    public void onNotEnoughSpace(File logRootFolder);
  }
}
