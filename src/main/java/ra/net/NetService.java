package ra.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.Executor;
import ra.net.processor.CommandProcessorListener;
import ra.net.processor.CommandProcessorProvider;
import ra.net.request.Request;

/**
 * 使用ServerSocket 與 Socket處理發送與接收.
 *
 * @author Ray Li, Kevin Tsai
 */
public class NetService extends Thread implements NetServiceable, AutoCloseable {
  private ServerSocket serverSocket;
  private CommandProcessorListener<String> processorListener;
  private CommandProcessorProvider<String> processorProvider;
  private BufferedInputStream bufferedInputStream;
  private SendProcessor sendProcessor;
  private Executor sendPool;
  private boolean isRunning = true;
  private int timeout = 0;
  private int index;
  private int socketSoTimeout = 20000;

  private NetService() {}

  @Override
  public void run() {
    Request<String> request = new Request<>(index);
    byte[] data = TransmissionEnd.BYTES_ZERO;

    while (isRunning) {
      try {
        Socket socket = null;
        synchronized (serverSocket) {
          socket = serverSocket.accept();
        }
        if (sendProcessor != null) {
          sendProcessor.close();
        }
        sendProcessor = new SendProcessor(this, socket, timeout);
        sendPool.execute(sendProcessor);
        request.setSender(sendProcessor);
        socket.setSoTimeout(socketSoTimeout);
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());

      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }
      boolean readThread = true;
      try {
        request.setIp(sendProcessor.getIp());
        processorListener = processorProvider.createCommand();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1];

        while (readThread) {
          int c = 0;
          while ((c = bufferedInputStream.read(buffer, 0, 1)) != -1) {
            byte flag = buffer[c - 1];
            if (flag == TransmissionEnd.FORM_FEED.getByte()
                || flag == TransmissionEnd.NEW_LINE.getByte()) {
              break;
            } else {
              baos.write(buffer, 0, c);
            }
          }

          request.setDataBytes(null);
          data = TransmissionEnd.BYTES_ZERO;
          if (baos.size() > 0) {
            data = baos.toByteArray();
          }
          baos.reset();
          if (c == -1) {
            data = null;
          }
          if (data != null && data.length == 0) {
            continue;
          }
          sendProcessor.setSoTimeout(timeout);

          if (data == null) {
            readThread = false;
            close();
            baos.close();
            baos = null;

            if (processorProvider != null) {
              processorProvider.offline(index);
            }
          } else {
            request.setDataBytes(data);

            if (processorListener != null) {
              processorListener.commandProcess(request);
            }
          }
        }
      } catch (IOException e) {
        readThread = false;
        close();
      }
    }
  }

  public void setCommandProcessorProvider(CommandProcessorProvider<String> provider) {
    this.processorProvider = provider;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public void onClose() {
    offline();
  }

  private void offline() {
    if (processorProvider == null) {
      return;
    }
    try {
      this.processorProvider.offline(index);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /** 關閉SendThread & 發送offline(). */
  @Override
  public void close() {
    try {
      sendProcessor.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    isRunning = false;
    offline();
  }

  // 將資料送出
  @Override
  public void send(String msg) {
    sendProcessor.send(msg);
  }

  // 送完資料後自動斷線
  @Override
  public void sendClose(String msg) {
    sendProcessor.sendClose(msg);
  }

  /**
   * 預設UTF-8.
   *
   * @param serverSocket ServerSocket原型
   * @param provider PolicyProvider
   * @param cmdProvider 提供處理command的實體物件
   * @param codeProvider 提供command編解碼、split的實體物件
   */
  public static class Builder {
    private ServerSocket serverSocket;
    private Executor sendPool;
    private CommandProcessorListener<String> commandProcessorListener;
    private int index;
    private Long socketSoTimeout;

    /** build. */
    public NetService build() {
      NetService service = new NetService();

      service.serverSocket = serverSocket;
      service.sendPool = sendPool;
      service.processorListener = commandProcessorListener;
      service.index = index;

      if (socketSoTimeout != null) {
        service.socketSoTimeout = socketSoTimeout.intValue();
      }
      return service;
    }

    public Builder setServerSocket(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
      return this;
    }

    public Builder setSendExecutor(Executor executor) {
      this.sendPool = executor;
      return this;
    }

    public Builder setIndex(int index) {
      this.index = index;
      return this;
    }

    public Builder setSocketSoTimeout(Duration socketSoTimeout) {
      this.socketSoTimeout = socketSoTimeout.toMillis();
      return this;
    }
  }

  @Override
  public boolean getSendcompilete() {
    return false;
  }

  @Override
  public void setSendcompilete(boolean compilete) {}
}
