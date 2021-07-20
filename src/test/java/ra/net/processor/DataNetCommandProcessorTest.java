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

    DataNetService.DataNetRequest.Builder builder = new DataNetService.DataNetRequest.Builder();
    Data data = new Data(expected);
    builder.setData(data);

    DataNetCommandProcessor obj =
        new DataNetCommandProcessor() {

          @Override
          public void commandProcess(DataNetService.DataNetRequest request) {
            assertArrayEquals(expected.getBytes(), request.getData().getContent());
            assertEquals(0, request.getIndex());
          }
        };

    obj.commandProcess(builder.build());
  }

  @Test
  public void testCommandEmpty() {
    String expected = "";

    DataNetService.DataNetRequest.Builder builder = new DataNetService.DataNetRequest.Builder();
    Data data = new Data(expected);
    builder.setData(data);

    DataNetCommandProcessor obj =
        new DataNetCommandProcessor() {

          @Override
          public void commandProcess(DataNetService.DataNetRequest request) {
            assertArrayEquals(expected.getBytes(), request.getData().getContent());
            assertEquals(0, request.getIndex());
          }
        };

    obj.commandProcess(builder.build());
  }
}
