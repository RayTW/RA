package ra.db.parameter;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Properties;
import org.junit.Test;
import ra.db.DatabaseCategory;

/** Test class. */
public class MysqlParametersTest {

  @Test
  public void testBuildParam() {
    final MysqlParameters obj =
        new MysqlParameters.Builder()
            .setHost("127.0.0.1")
            .setName("dbName")
            .setPassword("xxx")
            .setPort(0)
            .setProperties(() -> null)
            .setType("")
            .setUser("user")
            .setProfileSql(true)
            .build();

    Properties properties = new Properties();
    properties.put("useUnicode", true);
    properties.put("profileSQL", true);
    properties.put("characterEncoding", "utf8");
    properties.put("connectTimeout", 180000);
    properties.put("socketTimeout", 180000);

    assertThat(obj.getDatabaseUrl(), startsWith("jdbc:mysql://127.0.0.1:0/dbName"));
    assertEquals(obj.getHost(), "127.0.0.1");
    assertEquals(obj.getCategory(), DatabaseCategory.MYSQL);
    assertEquals(obj.getName(), "dbName");
    assertEquals(obj.getPassword(), "xxx");
    assertEquals(obj.getPort(), 0);
    assertEquals(obj.getUser(), "user");
    assertEquals(obj.getProperties().get("profileSQL"), "true");
    assertEquals(obj.getType(), "");
    assertEquals(obj.getDriver(), DatabaseCategory.MYSQL.getDriver());

    assertNotNull(obj);
  }

  @Test
  public void testParamSetName() {
    final MysqlParameters obj =
        new MysqlParameters.Builder()
            .setHost("127.0.0.1")
            .setName("dbName")
            .setPassword("xxx")
            .setPort(0)
            .build();

    obj.setName("abc");

    assertEquals(obj.getName(), "abc");
  }

  @Test
  public void testParamToString() {
    final MysqlParameters obj =
        new MysqlParameters.Builder()
            .setHost("127.0.0.1")
            .setName("dbName")
            .setPassword("xxx")
            .setPort(0)
            .setProperties(null)
            .build();

    assertNotNull(obj.toString());
  }
}
