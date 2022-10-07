package ra.db.parameter;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Properties;
import org.hamcrest.CoreMatchers;
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
    assertThat(obj.getDatabaseUrl(), CoreMatchers.containsString("profileSQL=true"));
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
            .build();

    assertNotNull(obj.toString());
  }

  @Test
  public void testToBuilder() {
    MysqlParameters obj =
        new MysqlParameters.Builder()
            .setHost("127.0.0.1")
            .setName("dbName")
            .setPassword("xxx")
            .setProfileSql(false)
            .setProperties("test", "vvvv")
            .setUser("user")
            .setPort(78)
            .build();

    MysqlParameters newParam = obj.toBuilder().build();

    assertEquals("127.0.0.1", newParam.getHost());
    assertEquals("dbName", newParam.getName());
    assertEquals("xxx", newParam.getPassword());
    assertEquals(78, newParam.getPort());
    assertEquals("user", newParam.getUser());
    assertEquals("jdbc:mysql://", newParam.getUrlSchema());
    assertNotNull(newParam.getDatabaseUrl());
  }
}
