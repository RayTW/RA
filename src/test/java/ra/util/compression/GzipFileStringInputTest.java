package ra.util.compression;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Test class. */
public class GzipFileStringInputTest {
  private File file = null;

  @Before
  public void setUp() throws Exception {
    file = new File("test.gz");
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
    GzipFileStringOutput output = new GzipFileStringOutput(file, "utf-8");
    String expected = "textlog";

    output.write(expected + "\n");
    output.close();

    try (GzipFileStringInput input = new GzipFileStringInput(file, "utf-8")) {
      assertEquals(expected, input.readLine());
    }
  }
}
