package ra.util.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ra.util.Utility;

/**
 * 定期 刪LOG.
 *
 * @author Kevin Tsai, Ray Li
 */
public class LogDelete extends Thread {
  private Duration intervalCheckTime = Duration.ofHours(1); // 預設為每一小時清一次��
  private SimpleDateFormat simpleDateFormat;
  private List<DelRecordSettings> logFileRoot = new CopyOnWriteArrayList<>();
  private Object lock = new Object();
  private boolean isRun = true;

  public LogDelete(String folderPath, int days) {
    this();
    logFileRoot.add(new DelRecordSettings(folderPath, days));
  }

  public LogDelete() {
    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  }

  /**
   * 新增LOG檔案資料夾路徑與要刪除幾天前的LOG檔案.
   *
   * @param folderPath 資料夾路徑
   * @param days 要刪除幾天前的(正值)
   */
  public void addLogPathWithSaveDays(String folderPath, int days) {
    removeLogPath(folderPath);
    logFileRoot.add(new DelRecordSettings(folderPath, days));
  }

  /**
   * 移除欲刪除指定LOG檔案資料夾路徑.
   *
   * @param folderPath 資料夾路徑
   * @return 若有移除已存在的資料夾會回傳true
   */
  public boolean removeLogPath(String folderPath) {
    boolean ret = false;
    for (int i = logFileRoot.size() - 1; i >= 0; i--) {
      if (folderPath.equals(logFileRoot.get(i).logFileRoot)) {
        logFileRoot.remove(i);
        ret = true;
      }
    }

    return ret;
  }

  /**
   * 固定的間隔時間檢查是否刪除逾期存放日誌.
   *
   * @param interval 間隔時間
   */
  public void setCheckTime(Duration interval) {
    this.intervalCheckTime = interval;
  }

  /** 尋找多組指定資料夾下，比對檔名前綴若是與保留日期不相同則進行刪除. */
  private void delFiles() {
    DelRecordSettings settings = null;

    for (int i = logFileRoot.size() - 1; i >= 0; i--) {
      settings = logFileRoot.get(i);
      delFile(settings.logFileRoot, settings.saveDays);
    }
  }

  /**
   * 尋找指定資料夾下，比對檔名前綴若是與保留日期不相同則進行刪除.
   *
   * @param rootPath 被指定檢查的資料夾
   * @param dayCount 要刪除幾天前的(正值)
   */
  private void delFile(String rootPath, int dayCount) {
    // 要保留不刪除的LOG日期
    ArrayList<String> keepDates = new ArrayList<>();
    // 被指定檢查的資料夾
    File[] logFileRoot = new File(rootPath).listFiles();

    if (logFileRoot != null) {
      // 預算出此次要檢查保留log檔的日期列表
      for (int i = dayCount - 1; i >= 0; i--) {
        keepDates.add(getDate(-i));
      }

      boolean isKeepFile;

      for (File file : logFileRoot) {
        isKeepFile = false;

        // 找到的檔案是否在保留名單
        for (String keep : keepDates) {
          if (file.getName().startsWith(keep)) {
            isKeepFile = true;
            break;
          }
        }

        // 不在保留名單，執行刪除檔案
        if (!isKeepFile) {
          Utility.get().deleteFiles(file);
        }
      }
    } else {
      System.out.println("LogDelete, no log file->" + logFileRoot);
    }
  }

  @Override
  public void run() {
    while (isRun) {
      delFiles();

      synchronized (lock) {
        try {
          lock.wait(intervalCheckTime.toMillis());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void close() {
    isRun = false;
    requestDelete();
  }

  /**
   * 取得加減過的日期，格式"2017-10-16".
   *
   * @param amount 加減值
   */
  private String getDate(int amount) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, amount);
    return simpleDateFormat.format(cal.getTime());
  }

  /** 請求進行刪log. */
  public void requestDelete() {
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  @Override
  public String toString() {
    return logFileRoot.toString();
  }

  private class DelRecordSettings {
    private String logFileRoot; // 檔案儲存路徑
    private int saveDays = 7; // 檔案預留天數

    public DelRecordSettings(String folderPath, int days) {
      logFileRoot = folderPath;
      saveDays = days;
    }

    @Override
    public String toString() {
      return "logFileRoot[" + logFileRoot + "],saveDays[" + saveDays + "]";
    }
  }
}
