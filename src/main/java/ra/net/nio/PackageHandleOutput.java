package ra.net.nio;

import static ra.net.nio.DataPackageProtocol.END_PACKAGE;
import static ra.net.nio.DataPackageProtocol.HEADER_LENGTH;
import static ra.net.nio.DataPackageProtocol.MTU;
import static ra.net.nio.DataPackageProtocol.PACKAGE;

import java.io.IOException;
import java.net.SocketException;

/**
 * 處理將要傳送出去的資料轉為bytes，再用封包格式輸出.
 *
 * <p>封包設計
 *
 * <pre>
 * |封包類型|封包長度|封包結尾|data|
 * |2bytes|2bytes|1byte|65535 bytes|
 *
 * 封包類型 :
 * > 0x00 0x00 text
 * > 0x00 0x10 zip
 *
 * end of package :
 * > 0x00 封包結尾
 * > 0x01 還有封包
 * </pre>
 *
 * @author Ray Li
 */
public class PackageHandleOutput implements Transfer {
  private byte[] buffer;

  public PackageHandleOutput() {
    buffer = new byte[HEADER_LENGTH + MTU];
  }

  @Override
  public void transfer(Data data, Writable consumer) throws SocketException, IOException {
    writeSplitBytes(data.getRaw(), data.getDataType().getType(), consumer);
  }

  private void writeSplitBytes(byte[] data, int dataType, Writable consumer)
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

      // 放封包header
      buffer[0] = (byte) (dataType >> 8);
      buffer[1] = (byte) dataType;
      buffer[2] = (byte) (packageLength >> 8);
      buffer[3] = (byte) (packageLength);
      buffer[4] = remainingLength > 0 ? PACKAGE : END_PACKAGE;

      // 放入封包data
      System.arraycopy(data, offset, buffer, HEADER_LENGTH, packageLength);
      offset += packageLength;

      if (consumer != null) {
        consumer.write(buffer, 0, HEADER_LENGTH + packageLength);
      }
    }
  }
}
