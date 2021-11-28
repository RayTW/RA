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
 * Provider socket write and read.
 *
 * @author Ray Li, Kevin Tsai
 */
public class NetService extends Thread implements NetServiceable, AutoCloseable {
  private ServerSocket serverSocket;
  private CommandProcessorListener<NetRequest> processorListener;
  private CommandProcessorProvider<NetRequest> processorProvider;
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
    NetRequest.Builder builder = new NetRequest.Builder();
    String data = "";

    builder.setIndex(index);

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
        builder.setSender(sendProcessor);
        socket.setSoTimeout(socketSoTimeout);
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());

      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }
      boolean readThread = true;
      try {
        builder.setIp(sendProcessor.getIp());
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

          data = "";
          if (baos.size() > 0) {
            data = new String(baos.toByteArray());
          }
          baos.reset();
          if (c == -1) {
            data = null;
          }
          if (data != null && data.isEmpty()) {
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
            builder.setText(data);

            if (processorListener != null) {
              processorListener.commandProcess(builder.build());
            }
          }
        }
      } catch (IOException e) {
        readThread = false;
        close();
      }
    }
  }

  /**
   * Set command processor.
   *
   * @param provider CommandProcessorProvider
   */
  public void setCommandProcessorProvider(CommandProcessorProvider<NetRequest> provider) {
    this.processorProvider = provider;
  }

  /**
   * Returns index.
   *
   * @return index
   */
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

  /** close. */
  @Override
  public void close() {
    try {
      if (sendProcessor != null) {
        sendProcessor.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    offline();
  }

  /** Send message. */
  @Override
  public void send(String message) {
    sendProcessor.send(message);
  }

  /** Close connection after sending message. */
  @Override
  public void sendClose(String message) {
    sendProcessor.sendClose(message);
  }

  /** builder. */
  public static class Builder {
    private ServerSocket serverSocket;
    private Executor sendPool;
    private CommandProcessorListener<NetRequest> commandProcessorListener;
    private int index;
    private Long socketSoTimeout;

    /**
     * build.
     *
     * @return build {@link NetService}
     */
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

    /**
     * Set ServerSocket.
     *
     * @param serverSocket serverSocket
     * @return Builder
     */
    public Builder setServerSocket(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
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
     * Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds.
     *
     * @param socketSoTimeout the specified timeout, in milliseconds.
     * @return Builder
     */
    public Builder setSocketSoTimeout(Duration socketSoTimeout) {
      this.socketSoTimeout = socketSoTimeout.toMillis();
      return this;
    }
  }

  @Override
  public boolean getSendCompilete() {
    return false;
  }

  @Override
  public void setSendCompilete(boolean compilete) {}

  /**
   * NetRequest.
   *
   * @author Ray Li
   */
  public static class NetRequest extends Request {
    private Sendable<String> sender;
    private String text;

    /**
     * Initialize.
     *
     * @param request request
     */
    public NetRequest(Request request) {
      super(request);
    }

    /**
     * Returns sender.
     *
     * @return sender
     */
    public Sendable<String> getSender() {
      return sender;
    }

    /**
     * Returns text.
     *
     * @return text
     */
    public String getText() {
      return text;
    }

    /**
     * builder.
     *
     * @author Ray Li
     */
    public static class Builder extends Request.Builder {
      private Sendable<String> sender;
      private String text;

      /**
       * Set message sender.
       *
       * @param sender sender
       * @return Builder
       */
      public Builder setSender(Sendable<String> sender) {
        this.sender = sender;

        return this;
      }

      /**
       * Set text.
       *
       * @param text text
       * @return Builder
       */
      public Builder setText(String text) {
        this.text = text;

        return this;
      }

      /** build. */
      @Override
      public NetRequest build() {
        NetRequest obj = new NetRequest(super.build());
        obj.sender = this.sender;
        obj.text = this.text;

        return obj;
      }
    }
  }
}
