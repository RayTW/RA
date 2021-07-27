package ra.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * It is used to observe the log of calling Exception.printStackTrace() in the process.
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
   * Returns instance of {@link CaughtExceptionHandler}.
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

  /**
   * To register listener event that caught exception.
   *
   * @param listener listener
   */
  public void setCaughtExceptionHandler(Consumer<Throwable> listener) {
    caughtExceptionHandler = listener;
  }

  /**
   * The following situations will trigger events:.
   *
   * <pre>
   * 1.When use System.err.println(..)
   * 2.When use {@link ra.db.parameter.MysqlParameters.Builder#setProfileSql(boolean)} set true.
   * </pre>
   *
   * @param listener error
   */
  public void setErrorConsoleOutputListener(Consumer<String> listener) {
    errorConsoleOutputListener = listener;
  }

  /**
   * Returns details of the error message.
   *
   * @param e Throwable
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
