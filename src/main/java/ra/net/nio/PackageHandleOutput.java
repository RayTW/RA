package ra.net.nio;

import static ra.net.nio.DataPackageProtocol.END_PACKAGE;
import static ra.net.nio.DataPackageProtocol.HEADER_LENGTH;
import static ra.net.nio.DataPackageProtocol.MTU;
import static ra.net.nio.DataPackageProtocol.PACKAGE;

import java.io.IOException;
import java.net.SocketException;

/**
 * Process package structure before sending.
 *
 * <p>package structure
 *
 * <pre>
 * |header|length|  end|data       |
 * |2bytes|2bytes|1byte|65535 bytes|
 *
 * flag :
 *  0x00 0x00 text
 *  0x00 0x10 zip
 *
 * end of package :
 *  0x00 package end
 *  0x01 has package
 * </pre>
 *
 * @author Ray Li
 */
public class PackageHandleOutput implements Transfer {
  private byte[] buffer;

  /** Initialize. */
  public PackageHandleOutput() {
    buffer = new byte[HEADER_LENGTH + MTU];
  }

  @Override
  public void transfer(Data data, Writable consumer) throws SocketException, IOException {
    writeSplitBytes(data.getDataType().getType(), data.toBytes(), consumer);
  }

  private void writeSplitBytes(int dataType, byte[] data, Writable consumer)
      throws SocketException, IOException {
    int remainingLength = data.length;
    int packageLength = 0;
    int offset = 0;

    while (remainingLength > 0) {
      if (remainingLength > MTU) {
        packageLength = MTU;
      } else {
        packageLength = remainingLength;
      }
      remainingLength -= MTU;

      if (remainingLength <= 0) {
        remainingLength = 0;
      }

      // put package header
      buffer[0] = (byte) (dataType >> 8);
      buffer[1] = (byte) dataType;
      buffer[2] = (byte) (packageLength >> 8);
      buffer[3] = (byte) (packageLength);
      buffer[4] = remainingLength > 0 ? PACKAGE : END_PACKAGE;

      // put package data
      System.arraycopy(data, offset, buffer, HEADER_LENGTH, packageLength);
      offset += packageLength;

      if (consumer != null) {
        consumer.write(buffer, 0, HEADER_LENGTH + packageLength);
      }
    }
  }
}
