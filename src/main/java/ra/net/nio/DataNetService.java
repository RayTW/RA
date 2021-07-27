package ra.net.nio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.Executor;
import ra.net.Sendable;
import ra.net.Serviceable;
import ra.net.processor.CommandProcessorListener;
import ra.net.processor.CommandProcessorProvider;
import ra.net.request.Request;

/**
 * Provide TCP/IP write with read use bytes, and the support text with file format transmission.
 *
 * @author Ray Li
 */
public class DataNetService extends Thread implements Serviceable<Data>, AutoCloseable {
  private ServerSocket serverSocket;
  private CommandProcessorListener<DataNetRequest> processorListener;
  private CommandProcessorProvider<DataNetRequest> processorProvider;
  private BufferedInputStream bufferedInputStream;
  private Sender<Data> sender;
  private Executor sendPool;
  private boolean isRunning = true;
  private int timeout = 0;
  private int index;
  private int socketSoTimeout = 20000;
  private Transfer transferListener;
  private PackageHandleInput input;

  private DataNetService() {
    input = new PackageHandleInput();
  }

  @Override
  public void run() {
    DataNetRequest.Builder builder = new DataNetRequest.Builder();

    builder.setIndex(index);

    while (isRunning) {
      try {
        Socket socket = null;
        synchronized (serverSocket) {
          socket = serverSocket.accept();
        }
        if (sender != null) {
          sender.close();
        }
        sender = new Sender<Data>(this, transferListener, socket, timeout);
        sendPool.execute(sender);
        builder.setSender(sender);
        socket.setSoTimeout(socketSoTimeout);
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());
      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }
      boolean readThread = true;
      try {
        builder.setIp(sender.getIp());
        processorListener = this.processorProvider.createCommand();

        while (readThread) {
          builder.setData(null);

          input.readByte(
              bufferedInputStream,
              (data) -> {
                builder.setData(data);

                return Boolean.TRUE;
              });

          sender.setSoTimeout(timeout);

          DataNetRequest request = builder.build();

          if (request.getData() == null) {
            readThread = false;
            close();
            processorProvider.offline(index);
          } else {
            processorListener.commandProcess(request);
          }
        }
      } catch (IOException e) {
        readThread = false;
        close();
      }
    }
  }

  /**
   * Returns index of service.
   *
   * @return index
   */
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

  /** Close service. */
  @Override
  public void close() {
    try {
      if (sender != null) {
        sender.close();
      }
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
    private CommandProcessorListener<DataNetRequest> processorListener;
    private CommandProcessorProvider<DataNetRequest> processorProvider;
    private int index;
    private Long socketSoTimeout;
    private Transfer transferListener;
    private Executor sendPool;

    /**
     * Initialize.
     *
     * @return {@link DataNetService}
     */
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

    /**
     * Set server socket.
     *
     * @param serverSocket serverSocket
     * @return Builder
     */
    public Builder setServerSocket(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
      return this;
    }

    /**
     * Set CommandProcessorProvider.
     *
     * @param provider provider
     * @return Builder
     */
    public Builder setCommandProcessorProvider(CommandProcessorProvider<DataNetRequest> provider) {
      this.processorProvider = provider;
      return this;
    }

    /**
     * Set index.
     *
     * @param index index
     * @return Builder
     */
    public Builder setIndex(int index) {
      this.index = index;
      return this;
    }

    /**
     * Set timeout.
     *
     * @param socketSoTimeout socketSoTimeout
     * @return Builder
     */
    public Builder setSocketSoTimeout(Duration socketSoTimeout) {
      this.socketSoTimeout = socketSoTimeout.toMillis();
      return this;
    }

    /**
     * Register listener.
     *
     * @param listener listener
     * @return Builder
     */
    public Builder setTransferListener(Transfer listener) {
      transferListener = listener;
      return this;
    }

    /**
     * Set executor service.
     *
     * @param executor executor
     * @return Builder
     */
    public Builder setSendExecutor(Executor executor) {
      this.sendPool = executor;
      return this;
    }
  }

  /** Sent to client text or file. */
  @Override
  public void send(Data message) {
    sender.send(message);
  }

  /** Close client connection after sent to client text or file. */
  @Override
  public void sendClose(Data message) {
    sender.sendClose(message);
  }

  @Override
  public void onClose() {
    offline();
  }

  /**
   * NetRequest.
   *
   * @author Ray Li
   */
  public static class DataNetRequest extends Request {
    private Sendable<Data> sender;
    private Data data;

    /**
     * Initialize.
     *
     * @param request request
     */
    public DataNetRequest(Request request) {
      super(request);
    }

    /**
     * Returns data.
     *
     * @return Sendable
     */
    public Sendable<Data> getSender() {
      return sender;
    }

    /**
     * Returns the data.
     *
     * @return Data
     */
    public Data getData() {
      return data;
    }

    /**
     * builder.
     *
     * @author Ray Li
     */
    public static class Builder extends Request.Builder {
      private Sendable<Data> sender;
      private Data data;

      /**
       * Set message sender.
       *
       * @param sender sender
       * @return Builder
       */
      public Builder setSender(Sendable<Data> sender) {
        this.sender = sender;

        return this;
      }

      /**
       * Set data.
       *
       * @param data data
       * @return Builder
       */
      public Builder setData(Data data) {
        this.data = data;

        return this;
      }

      /** build. */
      @Override
      public DataNetRequest build() {
        DataNetRequest obj = new DataNetRequest(super.build());
        obj.sender = this.sender;
        obj.data = this.data;

        return obj;
      }
    }
  }
}
