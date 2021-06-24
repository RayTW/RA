package ra.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 取得系統或目前Process的CPU、memory等等資訊.
 *
 * @author Ray Li
 */
public class Monitor {
  private OperatingSystemProxy operatingSystem;

  public Monitor() {
    operatingSystem = new OperatingSystemProxy();
  }

  /** 取得機器ip，比如 : 192.168.81.8，若取不到address時會回傳null */
  public String getLocalHostAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 取得機器ip，比如 : 192.168.81.8，若取不到address時會回傳feedback.
   *
   * @param feedback 取不到address時會回傳
   */
  public Object optLocalHostAddress(Object feedback) {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return feedback;
  }

  /** 取得機器InetAddress物件. */
  public InetAddress getLocalHost() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** 取得目前執行中Process佔用的CPU百分比，若回傳-1.0表示取不到CPU狀態，若有取到CPU數值則區間為0.0~100.0% */
  public double getProcessCpuLoad() {
    double cpuLoad = operatingSystem.getProcessCpuLoad();

    if (cpuLoad < 0.0) {
      return cpuLoad;
    }
    return cpuLoad * 100.0;
  }

  /** 取得目前系統已使用的CPU百分比，若回傳-1.0表示取不到CPU狀態，若有取到CPU數值則區間為0.0~100.0% */
  public double getSystemCpuLoad() {
    double cpuLoad = operatingSystem.getSystemCpuLoad();

    if (cpuLoad < 0.0) {
      return cpuLoad;
    }
    return cpuLoad * 100.0;
  }

  public long getCommittedVirtualMemory() {
    return operatingSystem.getCommittedVirtualMemory();
  }

  public long getFreePhysicalMemorySize() {
    return operatingSystem.getFreePhysicalMemorySize();
  }

  public long getFreeSwapSpace() {
    return operatingSystem.getFreeSwapSpace();
  }

  public long getProcessCpuTime() {
    return operatingSystem.getProcessCpuTime();
  }

  public long getTotalPhysicalMemory() {
    return operatingSystem.getTotalPhysicalMemory();
  }

  public long getTotalSwapSpaceSize() {
    return operatingSystem.getTotalSwapSpaceSize();
  }

  /** 取得目前執行中Process記憶體百分比，數值則區間為0.0~100.0% */
  public double getProcessMemoryLoad() {
    Runtime runtime = Runtime.getRuntime();
    // JVM空閒記憶體
    double freeM = runtime.freeMemory() / 1024.0 / 1024.0;
    // JVM最大可用記憶體
    double maxM = runtime.maxMemory() / 1024.0 / 1024.0;

    double tm = runtime.totalMemory() / 1024.0 / 1024.0;

    // 計算出JVM已用記憶體
    return ((tm - freeM) / maxM) * 100.0;
  }
}
