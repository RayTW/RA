package ra.util.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ra.util.Utility;

/** Test class. */
public class LogDeleteTest {
  private static File loadFile;

  /**
   * setUpBeforeClass.
   *
   * @throws Exception Exception.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    Path path = Paths.get("deleteLog");

    loadFile = path.toFile();

    if (Files.notExists(path)) {
      Files.createTempDirectory(loadFile.getPath());
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Utility.get().deleteFiles(loadFile);
  }

  @Test
  public void testAddDuplicatePath() {
    String path = loadFile.getPath();
    LogDelete obj = new LogDelete(path, 0);

    obj.addLogPathWithSaveDays(path, 0);

    assertTrue(obj.removeLogPath(path));
    assertFalse(obj.removeLogPath(path));
  }

  @Test
  public void testRemoveLogPathNotExists() {
    LogDelete obj = new LogDelete();

    assertFalse(obj.removeLogPath(loadFile.getPath()));
  }

  @Test
  public void testToString() {
    LogDelete obj = new LogDelete(loadFile.getPath(), 0);

    assertEquals("[logFileRoot[deleteLog],saveDays[0]]", obj.toString());
  }

  @Test
  public void testAutomaticDeleteLog() throws InterruptedException {
    AtomicInteger count = new AtomicInteger();
    String path = loadFile.getPath();
    LogSettings settings = new LogSettings();

    settings.setEnable(true);
    settings.setKeepDays(0);
    settings.setPath(path);

    LogEveryDay log = new LogEveryDay(true, settings, "utf-8");
    LogDelete obj =
        new LogDelete(path, settings.getKeepDays()) {
          public void requestDelete() {
            super.requestDelete();
            count.incrementAndGet();
          }
        };

    log.setLogEnable(true);
    obj.start();

    log.write("testLog\n");
    log.close();

    obj.requestDelete();
    obj.close();

    assertEquals(2, count.get());
  }

  @Test
  public void testKeepLog() throws InterruptedException {
    String path = loadFile.getPath();
    LogSettings settings = new LogSettings();

    settings.setEnable(true);
    settings.setKeepDays(2);
    settings.setPath(path);

    LogEveryDay log = new LogEveryDay(true, settings, "utf-8");
    LogDelete obj = new LogDelete(path, settings.getKeepDays());

    log.setLogEnable(true);
    obj.start();

    log.writeln("testLog");
    log.close();

    obj.requestDelete();

    Thread.sleep(20);
    obj.close();

    assertTrue(loadFile.list().length > 0);
  }
}
