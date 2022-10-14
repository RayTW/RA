package ra.db.parameter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import ra.db.DatabaseCategory;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class SpannerParametersTest {

  @Test
  public void testDuplicatedProjectId() {
    SpannerParameters param =
        SpannerParameters.newBuilder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .setProperties("ProjectID", "abc")
            .build();

    assertEquals(
        "jdbc:cloudspanner:/projects/abcd/instances/instance/databases/db", param.getDatabaseUrl());
  }

  @Test
  public void testHost() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .build();

    assertEquals("", param.getHost());
  }

  @Test
  public void testPort() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .build();

    assertEquals(0, param.getPort());
  }

  @Test
  public void testCredentialsPath() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
            .build();

    assertThat(
        param.getDatabaseUrl(),
        CoreMatchers.containsString(
            "jdbc:cloudspanner:/projects/abcd/instances/instance/databases/db?"
                + "credentials=C:\\SecureFiles\\ServiceKeyFile.p12;"));
  }

  @Test
  public void testNoSetProperties() {
    try {
      SpannerParameters.newBuilder().build();
    } catch (Exception e) {
      assertThat(e, instanceOf(IllegalArgumentException.class));
      assertEquals("Invalid argment", e.getMessage());
    }
  }

  @Test
  public void testNoSetProject() {
    try {
      new SpannerParameters.Builder()
          .setDatabaseId("db")
          .setInstanceId("instance")
          .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
          .build();
    } catch (Exception e) {
      assertThat(e, instanceOf(IllegalArgumentException.class));
      assertEquals("Project is a required argment.", e.getMessage());
    }
  }

  @Test
  public void testNoSetDatabase() {
    try {
      new SpannerParameters.Builder()
          .setProjectId("abcd")
          .setInstanceId("instance")
          .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
          .build();
    } catch (Exception e) {
      assertThat(e, instanceOf(IllegalArgumentException.class));
      assertEquals("Database is a required argment.", e.getMessage());
    }
  }

  @Test
  public void testNoSetInstance() {
    try {
      new SpannerParameters.Builder()
          .setProjectId("abcd")
          .setDatabaseId("db")
          .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
          .build();
    } catch (Exception e) {
      assertThat(e, instanceOf(IllegalArgumentException.class));
      assertEquals("Instance is a required argment.", e.getMessage());
    }
  }

  @Test
  public void testCategoryIsSpanner() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
            .build();

    assertEquals(DatabaseCategory.SPANNER, param.getCategory());
  }

  @Test
  public void testDriverIsSpanner() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
            .build();

    assertEquals(DatabaseCategory.SPANNER.getDriver(), param.getDriver());
  }

  @Test
  public void testToString() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .setCredentials("C:\\SecureFiles\\ServiceKeyFile.p12")
            .build();

    assertEquals(
        "jdbc:cloudspanner:/projects/abcd/instances/instance/databases/db?"
            + "credentials=C:\\SecureFiles\\ServiceKeyFile.p12;",
        param.toString());
  }

  @Test
  public void testToBuilder() {
    SpannerParameters param =
        new SpannerParameters.Builder()
            .setDatabaseId("db")
            .setInstanceId("instance")
            .setProjectId("abcd")
            .build();

    SpannerParameters newParam = param.toBuilder().setProjectId("project2").build();

    assertEquals(
        "jdbc:cloudspanner:/projects/project2/instances/instance/databases/db",
        newParam.getDatabaseUrl());
  }
}
