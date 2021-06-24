package ra.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/** Test class. */
public class JsonConfigParserTest {

  // example 1 : Load JSON file.
  @Test
  public void testInitConfigClass() {
    new JsonConfigParser().fill(Config.class, "unittest/config.json", true);

    Assert.assertEquals("unitest", Config.serverAlias);
    Assert.assertTrue(Config.socketPort == 12345);
    Assert.assertNotNull(Config.serverLogPath);
    Assert.assertNotNull(Config.json);
    Assert.assertNotNull(Config.jsonarray);
    Assert.assertEquals(1111111L, Config.longValue);
    Assert.assertEquals(12, Config.shortValue);
    Assert.assertEquals(0.25, Config.doubleValue, 0);
    Assert.assertEquals(0.25f, Config.floatValue, 0);
    Assert.assertEquals(true, Config.booleanValue);
  }

  // example 2 : Load two nest settings.
  @Test
  public void testInit2Class() {
    new JsonConfigParser()
        .fill(
            key -> {
              if ("config".equals(key)) {
                return Configs.Settings1.class;
              }
              if ("config2".equals(key)) {
                return Configs.Settings2.class;
              }
              return null;
            },
            "unittest/config2.json",
            true);

    Assert.assertEquals("myTest", Configs.Settings1.serverAlias);
    Assert.assertEquals(12345, Configs.Settings1.socketPort);
    Assert.assertNotNull(Configs.Settings1.serverLogPath);

    Assert.assertEquals("name", Configs.Settings2.yourName);
    Assert.assertEquals("1900-01-01", Configs.Settings2.birthday);
  }

  // example 3 :
  @Test // 這個用法不建議使用
  public void testInitConfig2KeyUsingTheSameClass() {
    new JsonConfigParser()
        .fill(
            key -> {
              return Config3.class;
            },
            "unittest/config2.json",
            true);

    Assert.assertEquals("myTest", Config3.serverAlias);
    Assert.assertTrue(Config3.socketPort == 12345);
    Assert.assertNotNull(Config3.serverLogPath);
    Assert.assertEquals("name", Config3.yourName);
    Assert.assertEquals("1900-01-01", Config3.birthday);
  }

  // example 4 :
  @Test
  public void testInit2KeyUsing2Class() {
    new JsonConfigParser()
        .fill(
            key -> {
              if ("config".equals(key)) {
                return ConfigSettings1.class;
              }
              if ("config2".equals(key)) {
                return ConfigSettings2.class;
              }
              return null;
            },
            "unittest/config2.json",
            true);

    Assert.assertEquals("myTest", ConfigSettings1.serverAlias);
    Assert.assertTrue(ConfigSettings1.socketPort == 12345);
    Assert.assertNotNull(ConfigSettings1.serverLogPath);

    Assert.assertEquals("name", ConfigSettings2.yourName);
    Assert.assertEquals("1900-01-01", ConfigSettings2.birthday);
  }

  // example 5
  @Test
  public void testInit3KeyUsing3Class() {
    new JsonConfigParser()
        .fill(
            key -> {
              if ("serverSettings".equals(key)) {
                return ServerSettings.class;
              }
              if ("dbSettings".equals(key)) {
                return DbSettings.class;
              }
              if ("jsonSettings".equals(key)) {
                return JsonSettings.class;
              }
              return null;
            },
            "unittest/config5.json",
            true);

    Assert.assertEquals("myTest", ServerSettings.serverAlias);
    Assert.assertTrue(ServerSettings.socketPort == 12345);
    Assert.assertNotNull(ServerSettings.serverLogPath);

    Assert.assertEquals("database", DbSettings.name);
    Assert.assertEquals("1.2.3.4", DbSettings.host);
    Assert.assertEquals(5, DbSettings.count);

    Assert.assertNotNull(JsonSettings.name);
    Assert.assertNotNull(JsonSettings.array);
    Assert.assertNotNull(JsonSettings.object);
  }

  // For example 1
  static class Config {
    public static final int CONST_VALUE = 11;

    public static String serverAlias = "";
    public static int socketPort;
    public static String serverLogPath;

    public static long longValue;
    public static short shortValue;
    public static boolean booleanValue;
    public static float floatValue;
    public static double doubleValue;
    public static JSONObject json;
    public static JSONArray jsonarray;
  }

  // For example 2
  static class Configs {
    public static class Settings1 {
      public static String serverAlias = "";
      public static int socketPort;
      public static String serverLogPath;
    }

    public static class Settings2 {
      public static String yourName;
      public static String birthday;
    }
  }

  // 範例3(不建議使用)
  static class Config3 {
    public static String serverAlias = "";
    public static int socketPort;
    public static String serverLogPath;
    public static String yourName;
    public static String birthday;
  }

  // For example 4
  static class ConfigSettings1 {
    public static String serverAlias = "";
    public static int socketPort;
    public static String serverLogPath;
  }

  static class ConfigSettings2 {
    public static String yourName;
    public static String birthday;
  }

  // For example 5
  static class ServerSettings {
    public static String serverAlias = "";
    public static int socketPort;
    public static String serverLogPath;
  }

  static class DbSettings {
    public static String name;
    public static String host;
    public static int count;
  }

  static class JsonSettings {
    public static String name;
    public static JSONObject object;
    public static JSONArray array;
  }
}
