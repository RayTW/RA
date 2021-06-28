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
 * Regular Delete Log file.
 *
 * @author Kevin Tsai, Ray Li
 */
public class LogDelete extends Thread {
  private Duration intervalCheckTime = Duration.ofHours(1); // Default every Hour.
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
   * Add the folder path to save Log file.
   * And add the Expiration date.
   *
   * @param folderPath the folder path to save Log file
   * @param days reserve days(positive number)
   */
  public void addLogPathWithSaveDays(String folderPath, int days) {
    removeLogPath(folderPath);
    logFileRoot.add(new DelRecordSettings(folderPath, days));
  }

  /**
   * Earmark the folder to Delete the Log file.
   *
   * @param folderPath the folder path to Delete the Log file
   * @return return true,when delete some exists files.
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
   * Checking the expired Log files each interval time, which is needed to delete.
   *
   * @param interval Checking interval time
   */
  public void setCheckTime(Duration interval) {
    this.intervalCheckTime = interval;
  }

  /** delete the expired Log files from all the saved folder. */
  private void delFiles() {
    DelRecordSettings settings = null;

    for (int i = logFileRoot.size() - 1; i >= 0; i--) {
      settings = logFileRoot.get(i);
      delFile(settings.logFileRoot, settings.saveDays);
    }
  }

  /**
   * delete the expired Log files from all the saved folder.
   *
   * @param rootPath the folder path which needs to checking
   * @param dayCount expired count by day(positive number)
   */
  private void delFile(String rootPath, int dayCount) {
    // the keep days
    ArrayList<String> keepDates = new ArrayList<>();
    // the folder path which needs to checking
    File[] logFileRoot = new File(rootPath).listFiles();

    if (logFileRoot != null) {
      //precount the keep day list
      for (int i = dayCount - 1; i >= 0; i--) {
        keepDates.add(getDate(-i));
      }

      boolean isKeepFile;

      for (File file : logFileRoot) {
        isKeepFile = false;

        // checking the keep list
        for (String keep : keepDates) {
          if (file.getName().startsWith(keep)) {
            isKeepFile = true;
            break;
          }
        }

        // delete file
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
   * Adds or subtracts the specified amount of time to the given now.
   * format style："2017-10-16".
   *
   * @param amount Adds or subtracts number
   */
  private String getDate(int amount) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, amount);
    return simpleDateFormat.format(cal.getTime());
  }

  /** Request that deletes Log file. */
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
    private String logFileRoot; // the path to save files
    private int saveDays = 7; // keep days

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
