package ra.net.nio;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ra.net.Sendable;
import ra.net.Serviceable;

/**
 * 將要傳送的訊息排queue 每100毫秒檢查1次.
 *
 * @author Ray Li
 */
public class Sender<E> extends Thread implements Sendable<Data> {
  private Serviceable<E> netListener;
  private Transfer transferListener;
  private boolean isRunning = true;
  private List<Data> queue = Collections.synchronizedList(new ArrayList<Data>(4096));
  private Socket socket;
  private BufferedOutputStream bufferedOutputStream;
  private int timeOut = 0; // 發送完訊息後，會對socket setSoTimeout(mTimeOut);
  private boolean sendClose = false;

  /**
   * Initialize.
   *
   * @param listener 用來發送Close的事件
   * @param transferListener 發送資料用
   * @param socket 連線元件
   * @param timeout Time Out時間
   * @throws IOException 建立BufferedOutputStream失敗時拋出
   */
  public Sender(Serviceable<E> listener, Transfer transferListener, Socket socket, int timeout)
      throws IOException {
    netListener = listener;
    this.transferListener = transferListener;
    this.socket = socket;
    timeOut = timeout;
    bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
  }

  /** 清空Que裡存放的資料. */
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
      Data msg;
      while (queue.size() > 0) {
        try {
          msg = queue.get(0);
          sendDataThread(msg);
          queue.remove(0);

        } catch (Exception e) {
          e.printStackTrace();
          close();
          netListener.onClose();
          break;
        }
        msg = null;
      }
      try {
        if (sendClose) {
          close();
          netListener.onClose();
        }

        synchronized (this) {
          wait(50);
        }
      } catch (InterruptedException e) {
        // do not thing.
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    flushQue();
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

    netListener = null;
    transferListener = null;
  }

  /**
   * 傳送資料.
   *
   * @param data 要發送的資料
   */
  @Override
  public void send(Data data) {
    queue.add(data);
    synchronized (this) {
      notifyAll();
    }
  }

  // 送完資料後自動斷線
  @Override
  public void sendClose(Data data) {
    send(data);
    sendClose = true;
  }

  // 將資料送出
  private void sendDataThread(Data data) throws Exception {
    // data transfer to bytes
    transferListener.transfer(
        data,
        (pkg, offset, length) -> {
          if (pkg != null) {
            socket.setSoTimeout(10000);

            bufferedOutputStream.write(pkg, offset, length);
            bufferedOutputStream.flush();
            socket.setSoTimeout(timeOut);
          }
        });
  }

  // 若socket還未斷線，將queue裡的訊息send
  private void flushQue() {
    Data msg;
    while (queue.size() > 0) {
      try {
        msg = queue.get(0);
        sendDataThread(msg);
        queue.remove(0);
      } catch (Exception e) { // 若送出訊息發生IOException則不繼續執行清queue
        e.printStackTrace();
        break;
      }
      msg = null;
    }
  }

  /**
   * 取得ip.
   *
   * @return ip
   */
  public String getIp() {
    if (socket != null) {
      return socket.getInetAddress().toString().replaceAll("/", "");
    }
    return null;
  }

  /**
   * 設定timeout時間.
   *
   * @param timeout .
   * @throws SocketException 設定失敗時拋出
   */
  public void setSoTimeout(int timeout) throws SocketException {
    if (socket != null) {
      socket.setSoTimeout(timeout);
    }
  }

  public boolean isSendClose() {
    return sendClose;
  }
}
