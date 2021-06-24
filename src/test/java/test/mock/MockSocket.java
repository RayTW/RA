package test.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/** Test class. */
public class MockSocket extends Socket {
  public void write(int b) throws IOException {}

  @Override
  public synchronized void close() throws IOException {}

  @Override
  public InputStream getInputStream() throws IOException {
    return new InputStream() {

      @Override
      public int read() throws IOException {
        return 0;
      }
    };
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return new OutputStream() {

      @Override
      public void write(int b) throws IOException {}
    };
  }
}
