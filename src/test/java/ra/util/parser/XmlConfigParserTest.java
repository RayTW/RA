package ra.util.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test class. */
public class XmlConfigParserTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testInitConfigClass() {
    new XmlConfigParser().fill(ConfigXml.class, "unittest/Config.xml");

    assertEquals("單元測試", ConfigXml.SERVER_ALIAS);
    assertEquals(12345, ConfigXml.SOCKET_PORT);
    assertNotNull(ConfigXml.SERVER_LOG_PATH);

    assertEquals(111, ConfigXml.LONG_VALUE);
    assertEquals(22, ConfigXml.SHORT_VALUE);
    assertEquals(true, ConfigXml.BOOLEAN_VALUE);
    assertEquals(1.2, ConfigXml.FLOAT_VALUE, 2);
    assertEquals(3.14, ConfigXml.DOUBLE_VALUE, 2);
  }

  @Test
  public void testInitConfigClassUsingIgonreException() {
    new XmlConfigParser().fill(ConfigXml.class, "unittest/Config.xml", true);

    assertEquals("單元測試", ConfigXml.SERVER_ALIAS);
    assertEquals(12345, ConfigXml.SOCKET_PORT);
    assertNotNull(ConfigXml.SERVER_LOG_PATH);

    assertEquals(111, ConfigXml.LONG_VALUE);
    assertEquals(22, ConfigXml.SHORT_VALUE);
    assertEquals(true, ConfigXml.BOOLEAN_VALUE);
    assertEquals(1.2, ConfigXml.FLOAT_VALUE, 2);
    assertEquals(3.14, ConfigXml.DOUBLE_VALUE, 2);
  }

  @Test
  public void testInitConfigClassInvalidValue() {
    new XmlConfigParser().fill(ConfigErrorXml.class, "unittest/ConfigError.xml", false);

    assertEquals(0, ConfigErrorXml.LONG_VALUE);
  }

  @Test
  public void testFillThrowUnsupportedOperationException() {
    exceptionRule.expect(UnsupportedOperationException.class);
    new XmlConfigParser()
        .fill(
            (clazz) -> {
              return null;
            },
            "unittest/Config.xml",
            false);
  }

  static class ConfigXml {
    public static String SERVER_ALIAS = "";
    public static int SOCKET_PORT;
    public static String SERVER_LOG_PATH;
    public static long LONG_VALUE;
    public static short SHORT_VALUE;
    public static boolean BOOLEAN_VALUE;
    public static float FLOAT_VALUE;
    public static double DOUBLE_VALUE;
  }

  static class ConfigErrorXml {
    public static long LONG_VALUE;
  }
}
