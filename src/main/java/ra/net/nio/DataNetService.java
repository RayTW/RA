package ra.net.nio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.Executor;
import ra.net.Serviceable;
import ra.net.processor.CommandProcessorListener;
import ra.net.processor.CommandProcessorProvider;
import ra.net.request.Request;
import ra.ref.BiReference;

/**
 * 使用ServerSocket 與 Socket處理發送與接收.
 *
 * @author Ray Li
 */
public class DataNetService extends Thread implements Serviceable<Data>, AutoCloseable {
  private ServerSocket serverSocket;
  private CommandProcessorListener<Data> processorListener;
  private CommandProcessorProvider<Data> processorProvider;
  private BufferedInputStream bufferedInputStream;
  private Sender<Data> sender;
  private Executor sendPool;
  private boolean isRunning = true;
  private int timeOut = 0;
  private int index;
  private int socketSoTimeout = 20000;
  private Transfer transferListener;
  private PackageHandleInput input;

  private DataNetService() {
    input = new PackageHandleInput();
  }

  @Override
  public void run() {
    BiReference<DataType, byte[]> ref = new BiReference<>();
    Request<Data> request = new Request<>(index);

    while (isRunning) {
      try {
        Socket socket = null;
        synchronized (serverSocket) {
          socket = serverSocket.accept();
        }
        if (sender != null) {
          sender.close();
        }
        sender = new Sender<Data>(this, transferListener, socket, timeOut);
        sendPool.execute(sender);
        request.setSender(sender);
        socket.setSoTimeout(socketSoTimeout);
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());
      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }
      boolean readThread = true;
      try {
        request.setIp(sender.getIp());
        processorListener = this.processorProvider.createCommand();

        while (readThread) {
          ref.setLeft(null);
          ref.setRight(null);

          input.readByte(
              bufferedInputStream,
              (dataType, dataBytes) -> {
                ref.setLeft(dataType);
                ref.setRight(dataBytes);

                return Boolean.TRUE;
              });

          if (ref.isLeftNull()) {
            continue;
          }

          sender.setSoTimeout(timeOut);
          if (ref.isRightNull()) {
            readThread = false;
            close();
            processorProvider.offline(index);
          } else {
            byte[] data = new byte[2 + ref.getRight().length];
            byte dateType = (byte) ref.getLeft().getType();

            DataType.copyToBytes(data, dateType);
            System.arraycopy(ref.getRight(), 0, data, 2, ref.getRight().length);

            request.setDataBytes(data);
            processorListener.commandProcess(request);
          }
        }
      } catch (IOException e) {
        readThread = false;
        close();
      }
    }
  }

  public int getIndex() {
    return index;
  }

  private void offline() {
    try {
      this.processorProvider.offline(index);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /** 關閉連線. */
  @Override
  public void close() {
    try {
      sender.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    isRunning = false;
    offline();
  }

  /**
   * builder.
   *
   * @author Ray Li
   */
  public static class Builder {
    private ServerSocket serverSocket;
    private CommandProcessorListener<Data> processorListener;
    private CommandProcessorProvider<Data> processorProvider;
    private int index;
    private Long socketSoTimeout;
    private Transfer transferListener;
    private Executor sendPool;

    /** Initialize. */
    public DataNetService build() {
      DataNetService service = new DataNetService();
      service.serverSocket = serverSocket;
      service.processorListener = processorListener;
      service.processorProvider = processorProvider;
      service.index = index;
      service.transferListener = transferListener;
      service.sendPool = sendPool;

      if (socketSoTimeout != null) {
        service.socketSoTimeout = socketSoTimeout.intValue();
      }
      return service;
    }

    public Builder setServerSocket(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
      return this;
    }

    public Builder setCommandProcessorProvider(CommandProcessorProvider<Data> provider) {
      this.processorProvider = provider;
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

    public Builder setTransferListener(Transfer listener) {
      transferListener = listener;
      return this;
    }

    public Builder setSendExecutor(Executor executor) {
      this.sendPool = executor;
      return this;
    }
  }

  @Override
  public void send(Data message) {
    sender.send(message);
  }

  @Override
  public void sendClose(Data message) {
    sender.sendClose(message);
  }

  @Override
  public void onClose() {
    offline();
  }
}
