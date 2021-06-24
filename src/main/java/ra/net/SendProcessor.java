package ra.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 將要傳送的訊息排queue 每100毫秒檢查1次.
 *
 * @author Ray Li, Kevin Tasi
 */
public class SendProcessor extends Thread implements Sendable<String> {
  private NetServiceable netEventListener;
  private boolean isRunning = true;
  private List<String> queue = Collections.synchronizedList(new ArrayList<String>());
  private Socket socket;
  private BufferedOutputStream bufferedOutputStream;
  private int timeOut = 0; // 發送完訊息後，會對socket setSoTimeout(mTimeOut);
  private boolean sendcompilete = false;

  /**
   * Initialize.
   *
   * @param net 發送訊息的Listener
   * @param socket Socket元件
   * @param timeout timeout時間
   * @throws IOException 建立BufferedOutputStream失敗時拋出
   */
  public SendProcessor(NetServiceable net, Socket socket, int timeout) throws IOException {
    netEventListener = net;
    this.socket = socket;
    this.timeOut = timeout;
    bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
  }

  public SendProcessor(NetServiceable net) {
    netEventListener = net;
  }

  /** 清空未發送的訊息queue. */
  public void clearQue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** 清空queue並停止送訊息thread. */
  public void close() {
    isRunning = false;
    try {
      synchronized (this) {
        notifyAll();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (isRunning) {
      String msg;
      while (queue.size() > 0) {
        try {

          msg = queue.get(0);
          sendDataThread(msg);
          queue.remove(0);

        } catch (Exception e) {
          close();
          netEventListener.onClose();
          e.printStackTrace();
          break;
        }
        msg = "";
      }
      try {
        if (sendcompilete) {
          close();
          netEventListener.onClose();
        }

        synchronized (this) {
          wait(100);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    flushQue(); // workaround，暫時解決sendthread因多緒搶進Sendcompilete=true之後，queue才被add訊息但無法送出
    clearQue();

    release();
  }

  private void release() {
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (bufferedOutputStream != null) {
      try {
        bufferedOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      bufferedOutputStream = null;
    }

    netEventListener = null;
  }

  /**
   * Send message.
   *
   * @param message message
   */
  @Override
  public void send(String message) {
    queue.add(message);
    synchronized (this) {
      notifyAll();
    }
  }

  /**
   * 送完資料後自動斷線.
   *
   * @param msg 訊息
   */
  @Override
  public void sendClose(String msg) {
    send(msg);
    setSendcompilete(true);
  }

  // 將資料送出
  private void sendDataThread(String msg) throws Exception {
    socket.setSoTimeout(10000);

    bufferedOutputStream.write((msg + "\f\n").getBytes());
    bufferedOutputStream.flush();
    socket.setSoTimeout(timeOut);
  }

  // 若socket還未斷線，將queue裡的訊息send
  private void flushQue() {
    String msg;
    while (queue.size() > 0) {
      try {
        msg = queue.get(0);
        sendDataThread(msg);
        queue.remove(0);
      } catch (Exception e) { // 若送出訊息發生IOException則不繼續執行清queue
        e.printStackTrace();
        break;
      }
      msg = "";
    }
  }

  /** 取得連線IP位址. */
  public String getIp() {
    if (socket != null) {
      return socket.getInetAddress().toString().replaceAll("/", "");
    }
    return null;
  }

  /**
   * 設定timeout時間.
   *
   * @param timeout timeout時間
   * @throws SocketException 設定失敗時拋出
   */
  public void setSoTimeout(int timeout) throws SocketException {
    if (socket != null) {
      socket.setSoTimeout(timeout);
    }
  }

  public boolean getSendcompilete() {
    return sendcompilete;
  }

  private void setSendcompilete(boolean compilete) {
    sendcompilete = compilete;
  }
}
