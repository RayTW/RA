package ra.server.basis;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import ra.util.compression.CompressionMode;
import ra.util.logging.CompressionTimeUnit;
import ra.util.logging.LogSettings;

/** Test class. */
public class GlobalTest {

  @Test
  public void testSetServerLogEnable() {
    Global obj = new Global();

    obj.setServerLogEnable(
        () -> {
          LogSettings settings = new LogSettings();

          settings.setKeepDays(99);
          settings.setPath("");
          settings.setEnable(false);
          settings.setCompressionTimeUnit(CompressionTimeUnit.DAY);
          settings.setCompressionMode(CompressionMode.GZIP);

          return settings;
        });

    assertNotNull(obj.getServerLog());

    obj.getLogDelete().close();
  }

  @Test
  public void testSetErrorLogEnable() {
    Global obj = new Global();

    obj.setErrorLogEnable(
        () -> {
          LogSettings settings = new LogSettings();

          settings.setKeepDays(99);
          settings.setPath("");
          settings.setEnable(false);
          settings.setCompressionTimeUnit(CompressionTimeUnit.DAY);
          settings.setCompressionMode(CompressionMode.GZIP);

          return settings;
        });

    assertNotNull(obj.getErrorLog());

    obj.getLogDelete().close();
  }

  @Test
  public void testGetLogDelete() {
    Global obj = new Global();

    assertNotNull(obj.getLogDelete());
    obj.getLogDelete().close();
  }
}
