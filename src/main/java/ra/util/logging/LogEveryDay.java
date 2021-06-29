package ra.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import ra.util.SpaceUnit;
import ra.util.compression.CompressionMode;

/**
 * Using the Date to create a file every day, which be used to write Log. the following action: Keep
 * the file I/O connection and will not close the file I/O connection when writing log into a file,
 * after creating the Log file by earmark file name. Using the new Date to write a log, when the
 * Date is changed.
 *
 * @author Ray Li
 */
public class LogEveryDay {
  // Object of writing log
  private LogRecord logRecord;
  private SimpleDateFormat simpleDateFormat;
  // The Character encoding of Log file
  private String charset;
  // The Compression Mode of Log file
  private CompressionTimeUnit compressionTimeUnit;
  private CompressionMode compressionMode;
  private ReentrantLock lock = new ReentrantLock();
  private boolean logEnable;

  /**
   * .
   *
   * @param enable enable of write log into Log file
   * @param setting settings of Log Object
   * @param charset The Character encoding of Log file
   */
  public LogEveryDay(boolean enable, LogSettings setting, String charset) {
    if (setting != null) {
      compressionTimeUnit = setting.getCompressionTimeUnit();
      compressionMode = setting.getCompressionMode();
    }

    this.charset = charset;
    logEnable = enable;
    simpleDateFormat = new SimpleDateFormat(compressionTimeUnit.getPattern());
    logRecord = new LogRecord();
    initLogRecord(setting.getPath(), generateFileName());
  }

  private void initLogRecord(String folderPath, String fileName) {
    logRecord.enableDateFormat();
    logRecord.setCompressionMode(compressionMode);
    logRecord.init(folderPath, fileName, charset);
  }

  public void setLogEnable(boolean logEnable) {
    this.logEnable = logEnable;
  }

  /**
   * Get Year、Month、Day of Today(Not Thread-Safe).
   *
   * @return current date
   */
  public String getCurrentDate() {
    return simpleDateFormat.format(new Date());
  }

  /**
   * Using the current date as file name to create a file(Not Thread-Safe).
   *
   * @return current date as file name
   */
  public String generateFileName() {
    return getCurrentDate() + "." + compressionMode.getFilenameExtension();
  }

  /**
   * Using the new current date as the file name to open a file, when the current date to be
   * changed.(Not Thread-Safe).
   */
  private void checkDoChangedDay() {
    if (!logRecord.getLogFile().getName().equals(generateFileName())) {
      logRecord.close();
      initLogRecord(logRecord.getLogFile().getParent(), generateFileName());
    }
  }

  /**
   * Write log into the Log file.
   *
   * @param str message which needs to be record
   */
  public void write(String str) {
    if (logEnable) {
      lock.lock();
      try {
        checkDoChangedDay();
        logRecord.write(str);
      } finally {
        lock.unlock();
      }
    }
  }

  /**
   * Will have text wraps automatically when write the log into file.
   *
   * @param str message which needs to be record
   */
  public void writeln(String str) {
    if (logEnable) {
      lock.lock();
      try {
        checkDoChangedDay();
        logRecord.writeln(str);
      } finally {
        lock.unlock();
      }
    }
  }

  /**
   * Will have text wraps automatically when write the log into file.
   *
   * @param tag The Tag Message to add in the head
   * @param message message which needs to be record
   * @param index index value
   */
  public void writeln(String tag, String message, int index) {
    if (logEnable) {
      writeln(tag + "[" + message + "],index[" + index + "]");
    }
  }

  /**
   * Will have text wraps automatically when write the log into file.
   *
   * @param tag The Tag Message to add in the head
   * @param message message which needs to be record
   */
  public void writeln(String tag, String message) {
    if (logEnable) {
      writeln(tag + "[" + message + "]");
    }
  }

  /**
   * Setting the limit size of Log file.
   *
   * @param space numbers in unit
   * @param unit the unit of space
   */
  public void setMaxFileSize(long space, SpaceUnit unit) {
    logRecord.setMaxFileSize(space, unit);
  }

  public LogRecord getLogRecord() {
    return logRecord;
  }

  public void close() {
    logRecord.close();
  }
}
