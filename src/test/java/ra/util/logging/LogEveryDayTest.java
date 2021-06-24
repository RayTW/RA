package ra.util.logging;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.AfterClass;
import org.junit.Test;
import ra.util.SpaceUnit;
import ra.util.Utility;
import ra.util.compression.CompressionMode;

/** Test class. */
public class LogEveryDayTest {
  private static final String ROOT_FOLDER = "./log/";

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Utility.get().deleteFiles(ROOT_FOLDER);
  }

  @Test
  public void testWriteLogOverReservedSpace() {
    String filePath = ROOT_FOLDER + "loggingTest";
    LogSettings setting = new LogSettings();

    setting.setPath(filePath);

    LogEveryDay log = new LogEveryDay(false, setting, "UTF-8");
    log.setLogEnable(true);

    int maxFileSize = 10;
    log.setMaxFileSize(maxFileSize, SpaceUnit.MB);

    System.out.println("預留空間[" + maxFileSize + SpaceUnit.MB + "]");
    log.getLogRecord()
        .setOnNotEnoughSpaceListener(
            (folder) -> {
              System.out.println("日誌檔案大小超過限制");
            });

    String textLog = "我是log";

    for (int i = 0; i < 10; i++) {
      textLog += textLog;
    }

    while (log.getLogRecord().hasRemaining()) {
      log.writeln(textLog);
    }

    log.writeln(textLog);

    log.close();

    long actual = 0;
    try (Stream<Path> stream = Files.list(Paths.get(filePath))) {
      File logFile =
          stream
              .filter(f -> f.toFile().getName().lastIndexOf(".log") != -1)
              .findFirst()
              .get()
              .toFile();

      actual = SpaceUnit.MB.convert(logFile.length(), SpaceUnit.Bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals(10L, actual);

    Utility.get().deleteFiles(filePath + "/");
  }

  @Test
  public void testWriteLogUsingCompressionGzip() {
    String filePath = ROOT_FOLDER + "loggingTest/gzip";
    LogSettings settings = new LogSettings();

    settings.setPath(filePath);
    settings.setCompressionTimeUnit(CompressionTimeUnit.HOUR);
    settings.setCompressionMode(CompressionMode.GZIP);

    LogEveryDay log = new LogEveryDay(false, settings, "UTF-8");

    log.setLogEnable(true);

    String textLog = "我是log";

    for (int i = 0; i < 1000; i++) {
      log.writeln(textLog);
    }

    log.close();

    Utility.get().deleteFiles(filePath + "/");
  }

  @Test
  public void testWriteLogUsingCompressionSnappy() {
    String filePath = ROOT_FOLDER + "loggingTest/snappy";
    LogSettings settings = new LogSettings();

    settings.setPath(filePath);
    settings.setCompressionTimeUnit(CompressionTimeUnit.HOUR);
    settings.setCompressionMode(CompressionMode.SNAPPY);
    LogEveryDay log = new LogEveryDay(false, settings, "UTF-8");

    log.setLogEnable(true);

    String textLog = "我是log";

    for (int i = 0; i < 1000; i++) {
      log.writeln(textLog);
    }

    log.close();

    Utility.get().deleteFiles(filePath + "/");
  }

  @Test
  public void testWriteLogUsingTagMessageIndex() {
    String filePath = ROOT_FOLDER + "loggingTest/snappy";
    LogSettings settings = new LogSettings();

    settings.setPath(filePath);
    settings.setCompressionTimeUnit(CompressionTimeUnit.HOUR);
    settings.setCompressionMode(CompressionMode.LOG);
    LogEveryDay log = new LogEveryDay(true, settings, "UTF-8");

    log.setLogEnable(true);

    log.writeln("tag", "我是log", 1);

    log.close();
    String actual = Utility.get().readFile(new File(filePath).listFiles()[0].getPath());

    Utility.get().deleteFiles(filePath + "/");

    assertThat(actual, containsString("tag[我是log],index[1]"));
  }

  @Test
  public void testWriteLogUsingTagMessage() {
    String filePath = ROOT_FOLDER + "loggingTest/snappy";
    LogSettings settings = new LogSettings();

    settings.setPath(filePath);
    settings.setCompressionTimeUnit(CompressionTimeUnit.HOUR);
    settings.setCompressionMode(CompressionMode.LOG);
    LogEveryDay log = new LogEveryDay(false, settings, "UTF-8");

    log.setLogEnable(true);

    log.writeln("tag", "我是log");

    log.close();
    String actual = Utility.get().readFile(new File(filePath).listFiles()[0].getPath());

    Utility.get().deleteFiles(filePath + "/");

    assertThat(actual, containsString("tag[我是log]"));
  }
}
