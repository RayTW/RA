package ra.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Test class. */
public class OperatingSystemProxyTest {

  @Test
  public void testGetCommittedVirtualMemory() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getCommittedVirtualMemory() >= 0);
  }

  @Test
  public void testGetFreePhysicalMemorySize() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getFreePhysicalMemorySize() >= 0);
  }

  @Test
  public void testGetFreeSwapSpace() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getFreeSwapSpace() >= 0);
  }

  @Test
  public void testGetProcessCpuLoad() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getProcessCpuLoad() >= -1);
  }

  @Test
  public void testGetProcessCpuTime() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getProcessCpuTime() >= 0);
  }

  @Test
  public void testGetSystemCpuLoad() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getSystemCpuLoad() >= -1);
  }

  @Test
  public void testGetTotalPhysicalMemory() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getTotalPhysicalMemory() >= 0);
  }

  @Test
  public void testGetTotalSwapSpaceSize() {
    OperatingSystemProxy obj = new OperatingSystemProxy();

    assertTrue(obj.getTotalSwapSpaceSize() >= 0);
  }
}
