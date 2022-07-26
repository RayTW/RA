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
    return getValue(MethodType.COMMITTED_VIRTUAL_MEMOTY_SIZE);
  }

  /**
   * Returns the amount of free physical memory in bytes.
   *
   * @return memory size
   */
  public long getFreePhysicalMemorySize() {
    return getValue(MethodType.FREE_PHYSICAL_MEMOTY_SIZE);
  }

  /**
   * Returns the amount of free swap space in bytes.
   *
   * @return swap space
   */
  public long getFreeSwapSpace() {
    return getValue(MethodType.FREE_SWAP_SPACE_SIZE);
  }

  /**
   * Get system CPU status.
   *
   * @return When get no CPU status will return -1.0.
   */
  public double getSystemCpuLoad() {
    return getValue(MethodType.SYSTEM_CPU_LOAD);
  }

  /**
   * Returns the CPU time used by the process on which the Java virtual machine is running in
   * nanoseconds.
   *
   * @return When get no CPU time will return 0.
   */
  public long getProcessCpuTime() {
    return getValue(MethodType.PROCESS_CPU_TIME);
  }

  /**
   * Returns the CPU percent used by the process.
   *
   * @return When get no CPU loading will return -1.0 .
   */
  public double getProcessCpuLoad() {
    return getValue(MethodType.PROCESS_CPU_LOAD);
  }

  /**
   * Returns the total amount of physical memory in bytes.
   *
   * @return memory
   */
  public long getTotalPhysicalMemory() {
    return getValue(MethodType.TOTAL_PHYSICAL_MEMORY_SIZE);
  }

  /**
   * Returns the total amount of swap space in bytes.
   *
   * @return memory
   */
  public long getTotalSwapSpaceSize() {
    return getValue(MethodType.TOTAL_SWAP_SPACE_SIZE);
  }

  @SuppressWarnings("unchecked")
  private <T> T getValue(MethodType type) {
    T result = null;

    Object obj = ManagementFactory.getOperatingSystemMXBean();
    try {
      Method method = methodsCache.get(type);
      result = (T) method.invoke(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
