package ra.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Test class. */
public class MonitorTest {

  @Test
  public void testGetProcessCpuLoad() {
    Monitor monitor = new Monitor();
    double cpuLoad = monitor.getSystemCpuLoad();

    assertTrue(-1.0 <= cpuLoad && cpuLoad <= 100.0);
  }

  @Test
  public void testGetSystemCpuLoad() {
    Monitor monitor = new Monitor();
    double cpuLoad = monitor.getSystemCpuLoad();

    assertTrue(-1.0 <= cpuLoad && cpuLoad <= 100.0);
  }

  @Test
  public void testGetLocalHostNotNull() {
    Monitor monitor = new Monitor();

    assertNotNull(monitor.getLocalHost());
  }

  @Test
  public void testGetLocalHostAddressNotNull() {
    Monitor monitor = new Monitor();

    assertNotNull(monitor.getLocalHostAddress());
  }
}
