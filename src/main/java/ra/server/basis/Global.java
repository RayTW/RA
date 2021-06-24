package ra.server.basis;

import java.time.Duration;
import java.util.function.Supplier;
import ra.util.logging.LogDelete;
import ra.util.logging.LogEveryDay;
import ra.util.logging.LogSettings;

/**
 * .
 *
 * @author Ray Li
 * @param <T> 定義傳送資料的型態
 */
public class Global {
  private LogEveryDay logEveryDay;
  private LogEveryDay errorLogEveryDay;
  private LogDelete logDelete;

  /** 創建. */
  public Global() {
    logDelete = new LogDelete();
    logDelete.setCheckTime(Duration.ofHours(1));
    // 啟動自動刪除過期log機制
    logDelete.start();
  }

  public void setServerLogEnable(Supplier<LogSettings> logEnable) {
    setServerLogEnable(logEnable.get());
  }

  /**
   * 設置日誌檔案路徑、儲存期間.
   *
   * @param logSettings 寫檔元件需要的相關設定
   */
  public void setServerLogEnable(LogSettings logSettings) {
    logEveryDay = new LogEveryDay(true, logSettings, "UTF-8");
    logEveryDay.setLogEnable(logSettings.getEnable());
    logDelete.addLogPathWithSaveDays(logSettings.getPath(), logSettings.getKeepDays());
    logDelete.requestDelete();
  }

  public void setErrorLogEnable(Supplier<LogSettings> errorLogEnable) {
    setErrorLogEnable(errorLogEnable.get());
  }

  /**
   * 設置錯誤日誌檔案路徑、儲存期間.
   *
   * @param logSettings 寫檔元件需要的相關設定
   */
  public void setErrorLogEnable(LogSettings logSettings) {
    errorLogEveryDay = new LogEveryDay(true, logSettings, "UTF-8");
    errorLogEveryDay.setLogEnable(logSettings.getEnable());
    logDelete.addLogPathWithSaveDays(logSettings.getPath(), logSettings.getKeepDays());
    logDelete.requestDelete();
  }

  public LogEveryDay getServerLog() {
    return logEveryDay;
  }

  public LogEveryDay getErrorLog() {
    return errorLogEveryDay;
  }

  public LogDelete getLogDelete() {
    return logDelete;
  }
}
