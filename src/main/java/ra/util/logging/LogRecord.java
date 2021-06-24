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
 * 建立檔案寫log 機制如下: 會以指定檔名進行創建檔案後，保持file I/O連線，每次寫入log之後不會關閉file I/O連線.
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
  private long maxFileSize; // 日誌檔大小限制
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
   * 可修改日期顯示的格式.
   *
   * @param format 顯示的格式
   */
  public void setSimpleDateFormat(String format) {
    simpleDateFormat = new SimpleDateFormat(format);
  }

  /** 開啟log前面串上日期、時間功能. */
  public void enableDateFormat() {
    dateFormatFlag = true;
  }

  /** 關閉log前面串上日期、時間功能. */
  public void disenableDateFormat() {
    dateFormatFlag = false;
  }

  /**
   * 設定寫入的日誌檔案壓縮格式.
   *
   * @param mode 壓縮方式
   */
  public void setCompressionMode(CompressionMode mode) {
    compressionMode = mode;
  }

  /**
   * 初始化.
   *
   * @param folderPath log的Folder路徑
   * @param fileName 檔名
   * @param charset 檔案的編碼格式
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

  /** . */
  public void close() {
    try {
      stringOutput.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 寫日誌到檔案，並且逐自動跳行..
   *
   * @param str 記錄的訊息
   */
  public void write(String str) {
    // 剩餘硬碟空間低於預留空間，不寫入log
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
        // 因為寫入失敗若時遇到硬碟空間不足時，若用e.printStackTrace()寫errorLog遞迴lock
        System.out.println(Utility.get().getThrowableDetail(e));
      }
    }
  }

  /**
   * 寫日誌到檔案，並且逐自動跳行.
   *
   * @param str log
   */
  public void writeln(String str) {
    write(str + System.lineSeparator());
  }

  /** 取得日誌檔名. */
  public File getLogFile() {
    return logFileName;
  }

  public File getLogFileFolder() {
    return logFileFolder;
  }

  /**
   * 設定日誌檔大小限制.
   *
   * @param space 數量
   * @param unit 空間的單位
   */
  public void setMaxFileSize(long space, SpaceUnit unit) {
    maxFileSize = SpaceUnit.Bytes.convert(space, unit);
  }

  /** 檢查日誌檔大小限制，是否還有剩餘空間可寫入.(預設無限制) */
  public boolean hasRemaining() {
    if (maxFileSize == 0L || logFileName.length() < maxFileSize) {
      return true;
    }

    return false;
  }

  /** 空間不夠寫入log時會呼叫. */
  public static interface OnNotEnoughSpaceListener {
    /**
     * 空間不夠寫入log時會呼叫.
     *
     * @param logRootFolder .
     */
    public void onNotEnoughSpace(File logRootFolder);
  }
}
