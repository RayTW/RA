package ra.util.logging;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import ra.util.SpaceUnit;
import ra.util.Utility;
import ra.util.compression.CompressionMode;

/** Test class. */
public class LogRecordTest {
  private static final String ROOT_FOLDER = "./log/";

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Utility.get().deleteFiles(ROOT_FOLDER);
  }

  @Test
  public void testWritelnUsingMaxFileSize1kb() {
    try {
      LogRecord logrecord = new LogRecord();
      logrecord.disenableDateFormat();
      logrecord.setMaxFileSize(1, SpaceUnit.KB);
      logrecord.init(ROOT_FOLDER + "loggingTest", "/log99.txt", "UTF-8");

      // write 4kb
      for (int i = 0; i < 100; i++) {
        logrecord.writeln("xxxxxx我是測我是国大坠陆人[" + i + "]");
      }
      logrecord.close();

      File logFile = new File(ROOT_FOLDER + "loggingTest", "/log99.txt");

      long actual = SpaceUnit.KB.convert(logFile.length(), SpaceUnit.Bytes);
      assertEquals(1L, actual);
    } finally {
      Utility.get().deleteFiles("./log/");
    }
  }

  @Test
  public void testWriteln() {
    try {
      LogRecord logrecord = new LogRecord();
      logrecord.disenableDateFormat();
      logrecord.init(ROOT_FOLDER + "loggingTest", "/log2.txt", "UTF-8");

      for (int i = 0; i < 100; i++) {
        logrecord.writeln("xxxxxx我是測我是国大坠陆人[" + i + "]");
      }
      logrecord.close();

      Assert.assertTrue(true);
    } finally {
      Utility.get().deleteFiles("./log/");
    }
  }

  @Test
  public void testWritelnUseLog() {
    try {
      LogRecord logrecord = new LogRecord();
      logrecord.disenableDateFormat();
      logrecord.setCompressionMode(CompressionMode.LOG);
      logrecord.init(ROOT_FOLDER + "loggingTest", "/log3.txt", "UTF-8");

      for (int i = 0; i < 10000; i++) {
        logrecord.writeln(
            "xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人["
                + i
                + "]");
      }
      logrecord.close();

      Assert.assertTrue(true);
    } finally {
      Utility.get().deleteFiles("./log/");
    }
  }

  @Test
  public void testWritelnUseCompressionGzip() {
    try {
      LogRecord logrecord = new LogRecord();
      logrecord.disenableDateFormat();
      logrecord.setCompressionMode(CompressionMode.GZIP);
      logrecord.init(ROOT_FOLDER + "loggingTest", "/log3.gz", "UTF-8");

      for (int i = 0; i < 10000; i++) {
        logrecord.writeln(
            "xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人["
                + i
                + "]");
      }
      logrecord.close();

      Assert.assertTrue(true);
    } finally {
      Utility.get().deleteFiles(ROOT_FOLDER + "loggingTest");
    }
  }

  @Test
  public void testWritelnUseCompressionSnappy() {
    try {
      LogRecord logrecord = new LogRecord();
      logrecord.disenableDateFormat();
      logrecord.setCompressionMode(CompressionMode.SNAPPY);
      logrecord.init(ROOT_FOLDER + "loggingTest", "/log3.sz", "UTF-8");

      for (int i = 0; i < 10000; i++) {
        logrecord.writeln(
            "xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx"
                + "我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人xxxxxx我是測我是国大坠陆人["
                + i
                + "]");
      }
      logrecord.close();

      Assert.assertTrue(true);
    } finally {
      Utility.get().deleteFiles("./log/");
    }
  }
}
