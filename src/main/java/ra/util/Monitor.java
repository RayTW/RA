package ra.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Monitor system process cup, memory status.
 *
 * @author Ray Li
 */
public class Monitor {
  private OperatingSystemProxy operatingSystem;

  public Monitor() {
    operatingSystem = new OperatingSystemProxy();
  }

  /**
   * Returns the local IP address, ex: 192.168.1.2, and when local is no address will return null.
   *
   * @return IP address
   */
  public String getLocalHostAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Returns the local IP address, ex: 192.168.1.2, and when local is no address will return
   * feedback.
   *
   * @param feedback If get address not found
   * @return IP address or feedback
   */
  public Object optLocalHostAddress(Object feedback) {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return feedback;
  }

  /**
   * Returns local address object.
   *
   * @return InetAddress
   */
  public InetAddress getLocalHost() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Returns current process CPU status.
   *
   * @return range (0.0~100.0%)
   */
  public double getProcessCpuLoad() {
    double cpuLoad = operatingSystem.getProcessCpuLoad();

    if (cpuLoad < 0.0) {
      return cpuLoad;
    }
    return cpuLoad * 100.0;
  }

  /**
   * Returns current system CPU status.
   *
   * @return range (0.0~100.0%)
   */
  public double getSystemCpuLoad() {
    double cpuLoad = operatingSystem.getSystemCpuLoad();

    if (cpuLoad < 0.0) {
      return cpuLoad;
    }
    return cpuLoad * 100.0;
  }

  /**
   * Returns the amount of virtual memory that is guaranteed to be available to the running process
   * in bytes, or -1 if this operation is not supported.
   *
   * @return virtual memory
   */
  public long getCommittedVirtualMemory() {
    return operatingSystem.getCommittedVirtualMemory();
  }

  /**
   * Returns the amount of free physical memory in bytes.
   *
   * @return free physical memory size
   */
  public long getFreePhysicalMemorySize() {
    return operatingSystem.getFreePhysicalMemorySize();
  }

  /**
   * Returns free swap space.
   *
   * @return swap space
   */
  public long getFreeSwapSpace() {
    return operatingSystem.getFreeSwapSpace();
  }

  /**
   * Returns the CPU time used by the process on which the Java virtual machine is running in
   * nanoseconds.
   *
   * @return When get no CPU time will return 0.
   */
  public long getProcessCpuTime() {
    return operatingSystem.getProcessCpuTime();
  }

  /**
   * Returns the total amount of physical memory in bytes.
   *
   * @return memory
   */
  public long getTotalPhysicalMemory() {
    return operatingSystem.getTotalPhysicalMemory();
  }

  /**
   * Returns the total amount of swap space in bytes.
   *
   * @return memory
   */
  public long getTotalSwapSpaceSize() {
    return operatingSystem.getTotalSwapSpaceSize();
  }

  /**
   * Returns current process memory status.
   *
   * @return range (0.0~100.0%)
   */
  public double getProcessMemoryLoad() {
    Runtime runtime = Runtime.getRuntime();
    // JVM free memory
    double freeM = runtime.freeMemory() / 1024.0 / 1024.0;
    // JVM max memory
    double maxM = runtime.maxMemory() / 1024.0 / 1024.0;

    double tm = runtime.totalMemory() / 1024.0 / 1024.0;

    // JVM used memory
    return ((tm - freeM) / maxM) * 100.0;
  }
}
