package ra.net.processor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import ra.net.nio.Data;
import ra.net.nio.DataType;
import ra.net.request.DataRequest;
import ra.net.request.Request;

/** Test class. */
public class CommandProcessorDataTest {

  @Test
  public void testCommandNotEmpty() {
    String expected = "message";

    Request<Data> request = new Request<>(0);

    byte[] msg = expected.getBytes();
    byte[] bytes = new byte[2 + msg.length];

    DataType.copyToBytes(bytes, DataType.TEXT.getType());
    System.arraycopy(msg, 0, bytes, 2, msg.length);

    request.setDataBytes(bytes);

    CommandProcessorData obj =
        new CommandProcessorData() {

          @Override
          public void commandHandle(DataRequest request) {
            assertArrayEquals(expected.getBytes(), request.getData().getRaw());
            assertEquals(0, request.getIndex());
          }
        };

    obj.commandProcess(request);
  }

  @Test
  public void testCommandEmpty() {
    String expected = "";

    Request<Data> request = new Request<>(0);

    byte[] msg = expected.getBytes();
    byte[] bytes = new byte[2 + msg.length];

    DataType.copyToBytes(bytes, DataType.TEXT.getType());
    System.arraycopy(msg, 0, bytes, 2, msg.length);

    request.setDataBytes(bytes);

    CommandProcessorData obj =
        new CommandProcessorData() {

          @Override
          public void commandHandle(DataRequest request) {
            assertArrayEquals(expected.getBytes(), request.getData().getRaw());
            assertEquals(0, request.getIndex());
          }
        };

    obj.commandProcess(request);
  }
}
