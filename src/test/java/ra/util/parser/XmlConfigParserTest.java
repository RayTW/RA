package ra.util.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.xml.stream.XMLStreamException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test class. */
public class XmlConfigParserTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testInitConfigClass() throws IllegalArgumentException, XMLStreamException {
    new XmlConfigParser().fill(ConfigXml.class, "unittest/config.xml");

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
  public void testInitConfigClassUsingIgonreException()
      throws IllegalArgumentException, XMLStreamException {
    new XmlConfigParser().fill(ConfigXml.class, "unittest/config.xml");

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
    ConfigErrorXml.longValue = 1;
    try {
      new XmlConfigParser().fill(ConfigErrorXml.class, "unittest/configError.xml");
    } catch (Exception e) {
      assertThat(e, instanceOf(IllegalArgumentException.class));
    }

    assertEquals(1, ConfigErrorXml.longValue);
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
    public static long longValue;
  }
}
