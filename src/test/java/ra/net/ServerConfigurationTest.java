package ra.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class ServerConfigurationTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();
  private String path = "unittest/application.properties";

  @Test
  public void testFileNotFound() throws IOException {
    exceptionRule.expect(IOException.class);
    new ServerConfiguration(UUID.randomUUID().toString());
  }

  @Test
  public void testGetString() throws IOException {
    ServerConfiguration config = new ServerConfiguration(path);

    assertEquals("1234", config.getProperty("server.port"));
  }

  @Test
  public void testGetStringMismatchKeyReturnDefaultValue() throws IOException {
    ServerConfiguration config = new ServerConfiguration(path);

    assertEquals("aaa", config.getProperty("abc", "aaa"));
  }

  @Test
  public void testGetInt() throws IOException {
    ServerConfiguration config = new ServerConfiguration(path);

    assertEquals(1234, config.getPropertyAsInt("server.port"));
  }

  @Test
  public void testGetIntMismatchKeyThrowIllegalArgumentException() throws IOException {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("Configuration no put key 'abc'.");

    ServerConfiguration config = new ServerConfiguration(path);
    config.getPropertyAsInt("abc");
  }

  @Test
  public void testGetIntMismatchKeyReturnDefaultValue() throws IOException {
    ServerConfiguration config = new ServerConfiguration(path);

    assertEquals(100, config.getPropertyAsInt("abc", 100));
  }

  @Test
  public void testGetAllPropertiesSize() throws IOException {
    ServerConfiguration config = new ServerConfiguration(path);

    assertFalse(config.getProperties().isEmpty());
  }
}
