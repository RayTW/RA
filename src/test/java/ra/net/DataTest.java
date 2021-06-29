package ra.net;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import org.junit.Test;
import ra.net.nio.Data;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class DataTest {

  @Test
  public void testFile() {
    Data data = new Data(Paths.get("unittest/testZip.properties"));

    assertEquals(
        "server.netservice.max-threads=10\n" + "server.port=1234\n", new String(data.getContent()));
  }

  @Test
  public void testParseFile() {
    Data data = new Data(Paths.get("unittest/testZip.properties"));
    Data result = Data.parse(data.getDataType(), data.toBytes());

    assertEquals(data.getTitle(), result.getTitle());
    assertArrayEquals(data.getContent(), result.getContent());
  }

  @Test
  public void testParseText() {
    Data data = new Data("test test aaabbb");
    Data result = Data.parse(data.getDataType(), data.toBytes());

    assertEquals(data.getTitle(), result.getTitle());
    assertArrayEquals(data.getContent(), result.getContent());
  }
}
