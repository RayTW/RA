package ra.util.compression;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test class. */
public class GzipFileStringInputTest {
  @Rule public ExpectedException thrown = ExpectedException.none();
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
  public void testWhenInitializeFileNotFoundException() throws IOException {
    thrown.expect(FileNotFoundException.class);
    new GzipFileStringInput(file, "utf-8").close();
  }

  @Test
  public void testReadGzipLogReadLine() throws IOException {
    GzipFileStringOutput output = new GzipFileStringOutput(file, "utf-8");
    String expected = "textlog";

    output.write(expected + "\n");
    output.close();

    try (GzipFileStringInput input = new GzipFileStringInput(file, "utf-8")) {
      assertEquals(expected, input.readLine());
    }
  }

  @Test
  public void testReadGzipLogRead() throws IOException {
    GzipFileStringOutput output = new GzipFileStringOutput(file, "utf-8");
    String expected = "a";
    output.write(expected);
    output.close();

    try (GzipFileStringInput input = new GzipFileStringInput(file, "utf-8")) {
      assertEquals(expected.charAt(0), input.read());
    }
  }
}
