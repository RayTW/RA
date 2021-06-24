package ra.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import ra.util.SpaceUnit;
import ra.util.compression.CompressionMode;

/**
 * 會用每天日期當檔名進行寫檔，開檔後會保持io不會重複開關寫檔串流.
 *
 * <p>機制如下: 1.寫檔採用LogRecord，有file I/O reuse 2.每次呼叫 write(String str)或writeln(String
 * str)寫log時，會檢查日期是否有變動，若變動將會以新日期為log檔名
 *
 * @author Ray Li
 */
public class LogEveryDay {
  // 寫檔用
  private LogRecord logRecord;
  private SimpleDateFormat simpleDateFormat;
  // log的存檔格式
  private String charset;
  // log壓縮設定
  private CompressionTimeUnit compressionTimeUnit;
  private CompressionMode compressionMode;
  private ReentrantLock lock = new ReentrantLock();
  private boolean logEnable;

  /**
   * .
   *
   * @param enable 關/開log是否寫檔
   * @param setting 日誌相關設定值
   * @param charset 文字編碼
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

  /** 取得今天年、月、日(Not Thread-Safe). */
  public String getCurrentDate() {
    return simpleDateFormat.format(new Date());
  }

  /** 以今天日期生成檔案名稱(Not Thread-Safe). */
  public String generateFileName() {
    return getCurrentDate() + "." + compressionMode.getFilenameExtension();
  }

  /** 檢查是否日期有變動、若變動將會關閉舊檔，開新檔案繼續寫檔(Not Thread-Safe). */
  private void checkDoChangedDay() {
    if (!logRecord.getLogFile().getName().equals(generateFileName())) {
      logRecord.close();
      initLogRecord(logRecord.getLogFile().getParent(), generateFileName());
    }
  }

  /**
   * 寫日誌到檔案.
   *
   * @param str 要記錄的訊息
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
   * 寫日誌到檔案，並且逐自動跳行..
   *
   * @param str 要記錄的訊息
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
   * 寫日誌到檔案，並且逐自動跳行.
   *
   * @param tag 記錄時要串上的標識訊息
   * @param message 要記錄的訊息
   * @param index 索引值
   */
  public void writeln(String tag, String message, int index) {
    if (logEnable) {
      writeln(tag + "[" + message + "],index[" + index + "]");
    }
  }

  /**
   * .
   *
   * @param tag 記錄時要串上的標識訊息
   * @param message 要記錄的訊息
   */
  public void writeln(String tag, String message) {
    if (logEnable) {
      writeln(tag + "[" + message + "]");
    }
  }

  /**
   * 設定日誌檔大小限制.
   *
   * @param space 數量
   * @param unit 空間的單位值
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
