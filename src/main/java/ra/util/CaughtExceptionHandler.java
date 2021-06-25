package ra.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;
import ra.db.parameter.MysqlParameters;

/**
 * 用於觀察process裡有呼叫Exception.printStackTrace()的log.
 *
 * @author Ray Li
 */
public class CaughtExceptionHandler {
  private static CaughtExceptionHandler instance;
  private static final String LINE_SEPARATOR = System.lineSeparator();
  private PrintStream console;
  private PrintStream errLog;
  private Consumer<Throwable> caughtExceptionHandler;
  private Consumer<String> errorConsoleOutputListener;

  private CaughtExceptionHandler() {
    initialize();
  }

  private void initialize() {
    console = System.err;

    OutputStream output =
        new OutputStream() {
          @Override
          public void write(int b) throws IOException {}
        };

    errLog =
        new PrintStream(output) {
          @Override
          public void println(Object obj) {
            this.println("" + obj);

            if (caughtExceptionHandler != null && obj instanceof Throwable) {
              Throwable e = (Throwable) obj;
              caughtExceptionHandler.accept(e);
            }
          }

          @Override
          public void println(String stackTrace) {
            console.println(stackTrace);

            if (errorConsoleOutputListener != null) {
              errorConsoleOutputListener.accept(stackTrace);
            }
          }
        };
    System.setErr(errLog);
  }

  /**
   * 取得Singleton的CaughtExceptionHandler.
   *
   * @return Returns instance of {@link CaughtExceptionHandler}
   */
  public static CaughtExceptionHandler get() {
    if (instance == null) {
      synchronized (CaughtExceptionHandler.class) {
        if (instance == null) {
          instance = new CaughtExceptionHandler();
        }
      }
    }

    return instance;
  }

  public void setCaughtExceptionHandler(Consumer<Throwable> listener) {
    caughtExceptionHandler = listener;
  }

  /**
   * 觸發這個事件傾聽者清況如下.:
   *
   * <pre>
   * 1.使用System.err.println(..)時
   * 2.使用 {@link MysqlParameters.Builder#setProfileSql(boolean)} 功能並且參數為true時
   * </pre>
   *
   * @param listener 將console取得的訊息透過listener拋出
   */
  public void setErrorConsoleOutputListener(Consumer<String> listener) {
    errorConsoleOutputListener = listener;
  }

  /**
   * 取得錯誤訊息的詳細資料.
   *
   * @param e 要被解折的Throwable
   * @return stack trance
   */
  public String getThrowableDetail(Throwable e) {
    StringBuilder errStr = new StringBuilder(e + LINE_SEPARATOR);

    for (StackTraceElement s : e.getStackTrace()) {
      errStr.append(s.toString());
      errStr.append(LINE_SEPARATOR);
    }
    return errStr.toString();
  }
}
