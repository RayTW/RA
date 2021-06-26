package ra.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import ra.net.processor.CommandProcessorListener;
import ra.net.processor.CommandProcessorProvider;
import ra.net.request.Request;

/**
 * Socket connection output.
 *
 * <pre>{@code
 * example:
 * NetSocketPrintKeep obj = new NetSocketPrintKeep.Builder()
 * .setHost("127.0.0.1")
 * .setPort(1234)
 * .setCommandProcessorProvider(...)
 * .build();
 *
 * obj.connectToSocket();
 * obj.start();
 * }</pre>
 *
 * @author Ray Li
 */
public class NetSocketWriterKeep extends Thread {
  private Socket socket;
  private boolean readable = true;
  private boolean isRunning = false;
  private String regedit = "";
  private String host = "";
  private int port;
  private int index;
  private BufferedOutputStream bufferedOutputStream;
  private BufferedInputStream bufferedInputStream;
  private CommandProcessorListener<String> commandProcessorListener;
  private SendProcessorKeep sendThreadKeep;
  private MessageReceiver receiveThread;
  private byte[] readBuffer = new byte[1024];
  private byte[] appendBuffer = new byte[readBuffer.length * 2];
  private ByteBuffer readByteBuffer = ByteBuffer.allocate(readBuffer.length * 2);
  private ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

  private NetSocketWriterKeep() {}

  private void setup(Boolean enableClearQueue) {
    sendThreadKeep = new SendProcessorKeep(this);
    if (enableClearQueue != null) {
      sendThreadKeep.enableClearQueue(enableClearQueue);
    }
    sendThreadKeep.start();

    Request<String> request = new Request<>(index);

    request.setSender(sendThreadKeep);

    receiveThread =
        new MessageReceiver(
            bytes -> {
              if (commandProcessorListener == null) {
                return;
              }
              request.setDataBytes(bytes);
              request.setIp(socket.getInetAddress().toString());
              commandProcessorListener.commandProcess(request);
            });
    receiveThread.start();
  }

  /** Connect. */
  public void connect() {
    try {
      socket = new Socket(host, port);
      bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
      bufferedInputStream = new BufferedInputStream(socket.getInputStream());
      if (!regedit.isEmpty()) {
        sendData(regedit);
      }
      if (!isRunning) {
        isRunning = true;
        start();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      try {
        sleep(1000);
        connect();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void run() {
    while (isRunning) {
      try {
        while (readable) {
          readLine(
              tmpData -> {
                if (tmpData == null) {
                  socket.close();
                  reconnect();
                } else {
                  receiveThread.putAndAwake(tmpData);
                }
              });
        }
      } catch (Exception e) {
        e.printStackTrace();
        try {
          if (socket != null) {
            socket.close();
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        reconnect();
      }
    }
  }

  private void readLine(OnReadLineListener listener) throws IOException {
    int len = 0;
    int offset = 0;

    while ((len = bufferedInputStream.read(readBuffer, 0, readBuffer.length)) != -1) {
      readByteBuffer.put(readBuffer, 0, len);
      offset = 0;

      for (int i = 0; i < readByteBuffer.position(); i++) {
        // find finish character '\f'
        if (readByteBuffer.get(i) == TransmissionEnd.FORM_FEED.getByte()) {
          offset = 1;
          // find finish character '\n' after '\f'
          if ((i + 1) < len && readByteBuffer.get(i + 1) == TransmissionEnd.NEW_LINE.getByte()) {
            offset = 2;
          }

          if (i > 0) {
            readByteBuffer.flip();
            readByteBuffer.get(appendBuffer, 0, i);
            readByteBuffer.compact();
            byteArray.write(appendBuffer, 0, i);
            i = 0; // find character '\f'
            String data = new String(byteArray.toString());
            byteArray.reset();
            listener.onReadLine(data);
          }
          // remove character "\f"、"\n"
          if (offset > 0 && readByteBuffer.position() >= 2) {
            readByteBuffer.flip();
            readByteBuffer.get(appendBuffer, 0, offset);
            readByteBuffer.compact();
            i = 0;
          }
        }
      }
      if (offset == 0) {
        readByteBuffer.flip();
        readByteBuffer.get(appendBuffer, 0, len);
        readByteBuffer.compact();
        byteArray.write(appendBuffer, 0, len);
      }
    }
    if (len == -1) {
      listener.onReadLine(null);
    }
  }

  private interface OnReadLineListener {
    public void onReadLine(String line) throws IOException;
  }

  public int getIndex() {
    return index;
  }

  public void setRegeditCode(String code) {
    this.regedit = code;
  }

  public String getRegeditCode() {
    return this.regedit;
  }

  private void reconnect() {
    try {
      sleep(1000);
      connect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendData(String msg) {
    sendThreadKeep.send(msg);
  }

  void write(String msg) throws IOException {
    bufferedOutputStream.write(TransmissionEnd.appendFeedNewLine(msg).getBytes());
    bufferedOutputStream.flush();
  }

  void close() {
    try {
      if (socket != null) {
        socket.close();
        socket = null;
      }
      System.out.println("[connect lost]");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /** build NetSocketPrintKeep. */
  public static class Builder {
    private String host;
    private int port;
    private int index;
    private Boolean enableClearQueue;
    private CommandProcessorProvider<String> commandProcessorProvider;

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

    public Builder setIndex(int index) {
      this.index = index;
      return this;
    }

    /**
     * Whether to enable clearing of cached messages after disconnection.
     *
     * @param enableClearQueue Default true
     * @return Builder
     */
    public Builder enableClearQueue(boolean enableClearQueue) {
      this.enableClearQueue = enableClearQueue;
      return this;
    }

    public Builder setCommandProcessorProvider(CommandProcessorProvider<String> provider) {
      commandProcessorProvider = provider;
      return this;
    }

    /**
     * Build NetSocketWriterKeep.
     *
     * @return {@link NetSocketWriterKeep}
     */
    public NetSocketWriterKeep build() {
      if (host == null || host.isEmpty()) {
        throw new IllegalArgumentException("host == null or host.isEmpty()");
      }

      if (port < 0 || port > 65535) {
        throw new IllegalArgumentException("The port number must be between 0 and 65535");
      }

      NetSocketWriterKeep obj = new NetSocketWriterKeep();

      obj.host = host;
      obj.port = port;
      obj.commandProcessorListener = commandProcessorProvider.createCommand();
      obj.index = index;

      obj.setup(enableClearQueue);

      return obj;
    }
  }
}
