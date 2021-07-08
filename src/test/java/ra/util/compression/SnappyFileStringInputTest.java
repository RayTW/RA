package ra.util.compression;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Test class. */
public class SnappyFileStringInputTest {
  private File file = null;

  @Before
  public void setUp() throws Exception {
    file = new File("test.snappy");
  }

  /**
   * Delete file.
   *
   * @throws Exception Exception
   */
  @After
  public void tearDown() throws Exception {
    if (file != null && file.exists()) {
      file.delete();
    }
  }

  @Test
  public void testReadGzipLog() throws IOException {
    SnappyFileStringOutput output = new SnappyFileStringOutput(file, "utf-8");
    String expected = "textlog";

    output.write(expected + "\n");
    output.close();

    try (SnappyFileStringInput input = new SnappyFileStringInput(file, "utf-8")) {
      assertEquals(expected, input.readLine());
    }
  }
}
