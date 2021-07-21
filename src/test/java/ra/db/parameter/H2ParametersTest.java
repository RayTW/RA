package ra.db.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ra.db.DatabaseCategory;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class H2ParametersTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Test
  public void testLocalFileModeUrl() {
    H2Parameters param =
        new H2Parameters.Builder()
            .setProperties(
                () -> {
                  Properties pro = new Properties();

                  pro.put("USER", "ray");
                  pro.put("PASSWORD", "abc");

                  return pro;
                })
            .localFile("file:./data/sample")
            .setName("myDb")
            .build();

    String actual = param.getDatabaseUrl();

    assertEquals("jdbc:h2:file:./data/sample/myDb;USER=ray;PASSWORD=abc", actual);
  }

  @Test
  public void testSetUserPassword() {
    H2Parameters param =
        new H2Parameters.Builder()
            .localFile("file:./data/sample")
            .setUser("ray")
            .setPassword("aabbcc")
            .build();

    assertEquals("ray", param.getUser());
    assertEquals("aabbcc", param.getPassword());
  }

  @Test
  public void testSetPropertiesUseNull() {
    H2Parameters param = new H2Parameters.Builder().setProperties(null).build();

    assertNull(param.getProperties());
  }

  @Test
  public void testDefaultSettings() {
    H2Parameters param = new H2Parameters.Builder().build();

    assertEquals(DatabaseCategory.H2, param.getCategory());
    assertEquals(DatabaseCategory.H2.getSchema(), param.getUrlSchema());
    assertEquals(DatabaseCategory.H2.getDriver(), param.getDriver());
    assertEquals("jdbc:h2:mem:", param.getDatabaseUrl());
    assertEquals("jdbc:h2:mem:", param.toString());
  }

  @Test
  public void testSetHostPort() {
    H2Parameters param =
        new H2Parameters.Builder()
            .localFile("file:./data/sample")
            .setHost("localhost")
            .setPort(1234)
            .build();

    assertEquals("localhost", param.getHost());
    assertEquals(1234, param.getPort());
  }

  @Test
  public void testInMemoryModeHasDbName() {
    H2Parameters param = new H2Parameters.Builder().inMemory().setName("myDb").build();
    String actual = param.getDatabaseUrl();

    assertEquals("jdbc:h2:mem:myDb", actual);
  }

  @Test
  public void testInMemoryModeNoDbName() {
    H2Parameters param = new H2Parameters.Builder().inMemory().build();
    String actual = param.getDatabaseUrl();

    assertEquals("jdbc:h2:mem:", actual);
  }
}
