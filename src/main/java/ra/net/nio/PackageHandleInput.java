package ra.net.nio;

import static ra.net.nio.DataPackageProtocol.END_PACKAGE;
import static ra.net.nio.DataPackageProtocol.HEADER_LENGTH;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

/**
 * Receive the packet and parse it.
 *
 * @author Ray Li
 */
public class PackageHandleInput {
  private ByteArrayOutputStream contextBuffer;
  private ByteArrayOutputStream headerBuffer;
  private byte[] buffer = new byte[4096];

  public PackageHandleInput() {
    contextBuffer = new ByteArrayOutputStream();
    headerBuffer = new ByteArrayOutputStream();
  }

  /**
   * Read bytes.
   *
   * @param in input
   * @param listener parse package
   * @throws IOException IOException
   */
  public void readByte(BufferedInputStream in, Function<Data, Boolean> listener)
      throws IOException {
    int len = 0;
    byte[] header = null;

    int packageLength = 0;
    int dataType = 0;

    headerBuffer.reset();
    contextBuffer.reset();

    while ((len = in.read(buffer, 0, 1)) != -1) {
      headerBuffer.write(buffer[0]);

      if (headerBuffer.size() >= HEADER_LENGTH) {
        header = headerBuffer.toByteArray();
        headerBuffer.reset();
        packageLength = 0;
        dataType = DataType.toInt(header);
        packageLength = (header[2] << 8) & 0x0000ff00 | (header[3] << 0) & 0x000000ff;
        int readLen = 0;
        len = 0;

        while (packageLength > 0) {
          readLen = Math.min(buffer.length, packageLength);
          len = in.read(buffer, 0, readLen);

          if (len == -1) {
            break;
          }

          contextBuffer.write(buffer, 0, len);

          packageLength -= len;
        }
        if (packageLength == 0 && header[4] == END_PACKAGE) {
          byte[] databyte = contextBuffer.toByteArray();

          contextBuffer.reset();

          if (listener != null) {
            if (listener.apply(Data.parse(DataType.valueOf(dataType), databyte))) {
              break;
            }
          }
        }
      }
    }
  }
}
