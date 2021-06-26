package ra.server.basis;

import java.time.Duration;
import java.util.function.Supplier;
import ra.util.logging.LogDelete;
import ra.util.logging.LogEveryDay;
import ra.util.logging.LogSettings;

/**
 * Provide logging.
 *
 * @author Ray Li
 */
public class Global {
  private LogEveryDay logEveryDay;
  private LogEveryDay errorLogEveryDay;
  private LogDelete logDelete;

  /** Initialize. */
  public Global() {
    logDelete = new LogDelete();
    logDelete.setCheckTime(Duration.ofHours(1));
    logDelete.start();
  }

  public void setServerLogEnable(Supplier<LogSettings> logEnable) {
    setServerLogEnable(logEnable.get());
  }

  /**
   * Set the log file path and storage period.
   *
   * @param logSettings logging settings.
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
   * Set the error log file path and storage period.
   *
   * @param logSettings logging settings.
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
