package ra.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.EnumMap;

/**
 * Proxy com.sun.management.OperatingSystemMXBean.
 *
 * @author Ray Li
 */
public class OperatingSystemProxy {
  private EnumMap<MethodType, Method> methodsCache;

  enum MethodType {
    COMMITTED_VIRTUAL_MEMOTY_SIZE("getCommittedVirtualMemorySize"),
    FREE_PHYSICAL_MEMOTY_SIZE("getFreePhysicalMemorySize"),
    FREE_SWAP_SPACE_SIZE("getFreeSwapSpaceSize"),
    PROCESS_CPU_LOAD("getProcessCpuLoad"),
    PROCESS_CPU_TIME("getProcessCpuTime"),
    SYSTEM_CPU_LOAD("getSystemCpuLoad"),
    TOTAL_PHYSICAL_MEMORY_SIZE("getTotalPhysicalMemorySize"),
    TOTAL_SWAP_SPACE_SIZE("getTotalSwapSpaceSize");

    private final String methodName;

    MethodType(String name) {
      methodName = name;
    }
  }

  /** Initialize. */
  public OperatingSystemProxy() {
    methodsCache = new EnumMap<>(MethodType.class);

    Class<?> clazz = null;
    try {
      clazz = Class.forName("com.sun.management.OperatingSystemMXBean");

      Method[] methods = clazz.getMethods();

      for (Method m : methods) {
        for (MethodType methodTypd : MethodType.values()) {
          if (m.getName().trim().equals(methodTypd.methodName.trim())) {
            methodsCache.put(methodTypd, m);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the amount of virtual memory that is guaranteed to be available to the running process
   * in bytes, or -1 if this operation is not supported.
   *
   * @return virtual memory
   */
  public long getCommittedVirtualMemory() {
    long result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.COMMITTED_VIRTUAL_MEMOTY_SIZE);
      result = (long) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Returns the amount of free physical memory in bytes.
   *
   * @return memory size
   */
  public long getFreePhysicalMemorySize() {
    long result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.FREE_PHYSICAL_MEMOTY_SIZE);
      result = (long) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Returns the amount of free swap space in bytes.
   *
   * @return swap space
   */
  public long getFreeSwapSpace() {
    long result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.FREE_SWAP_SPACE_SIZE);
      result = (long) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Get system CPU status.
   *
   * @return When get no CPU status will return -1.0.
   */
  public double getSystemCpuLoad() {
    double result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.SYSTEM_CPU_LOAD);
      result = (double) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (result < 0.0) {
      return result;
    }
    return result;
  }

  /**
   * Returns the CPU time used by the process on which the Java virtual machine is running in
   * nanoseconds.
   *
   * @return When get no CPU time will return 0.
   */
  public long getProcessCpuTime() {
    long result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.PROCESS_CPU_TIME);
      result = (long) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Returns the CPU percent used by the process.
   *
   * @return When get no CPU loading will return -1.0 .
   */
  public double getProcessCpuLoad() {
    double result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.PROCESS_CPU_LOAD);
      result = (double) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (result < 0.0) {
      return result;
    }
    return result;
  }

  /**
   * Returns the total amount of physical memory in bytes.
   *
   * @return memory
   */
  public long getTotalPhysicalMemory() {
    long result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.TOTAL_PHYSICAL_MEMORY_SIZE);
      result = (long) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Returns the total amount of swap space in bytes.
   *
   * @return memory
   */
  public long getTotalSwapSpaceSize() {
    long result = 0;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(MethodType.TOTAL_SWAP_SPACE_SIZE);
      result = (long) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
