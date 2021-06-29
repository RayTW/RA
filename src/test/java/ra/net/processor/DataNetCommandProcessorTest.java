package ra.net.processor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import ra.net.nio.Data;
import ra.net.nio.DataNetService;

/** Test class. */
public class DataNetCommandProcessorTest {

  @Test
  public void testCommandNotEmpty() {
    String expected = "message";

    DataNetService.NetDataRequest.Builder builder = new DataNetService.NetDataRequest.Builder();
    Data data = new Data(expected);
    builder.setData(data);

    DataNetCommandProcessor obj =
        new DataNetCommandProcessor() {

          @Override
          public void commandProcess(DataNetService.NetDataRequest request) {
            assertArrayEquals(expected.getBytes(), request.getData().getContent());
            assertEquals(0, request.getIndex());
          }
        };

    obj.commandProcess(builder.build());
  }

  @Test
  public void testCommandEmpty() {
    String expected = "";

    DataNetService.NetDataRequest.Builder builder = new DataNetService.NetDataRequest.Builder();
    Data data = new Data(expected);
    builder.setData(data);

    DataNetCommandProcessor obj =
        new DataNetCommandProcessor() {

          @Override
          public void commandProcess(DataNetService.NetDataRequest request) {
            System.out.println("==" + request.getData().getContent());
            assertArrayEquals(expected.getBytes(), request.getData().getContent());
            assertEquals(0, request.getIndex());
          }
        };

    obj.commandProcess(builder.build());
  }
}
