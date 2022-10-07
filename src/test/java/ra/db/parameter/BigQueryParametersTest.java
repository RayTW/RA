package ra.db.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.DatabaseCategory;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class BigQueryParametersTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testDuplicatedProjectIdOauhType() {
    BigQueryParameters param =
        BigQueryParameters.newBuilder("test", 1)
            .setProperties("ProjectId", "ray")
            .setProperties("OAuthType", "abc")
            .build();

    String actual = param.getDatabaseUrl();

    assertEquals(
        "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=test;OAuthType=1;",
        actual);
  }

  @Test
  public void testUsingGoogleServiceAccount() {
    BigQueryParameters param =
        BigQueryParameters.newBuilder("MyBigQueryProject", 0)
            .setProperties(
                "OAuthServiceAcctEmail", "bqtest1@data-driver-testing.iam.gserviceaccount.com")
            .setProperties("OAuthPvtKeyPath", "C:\\SecureFiles\\ServiceKeyFile.p12")
            .build();

    String actual = param.getDatabaseUrl();

    assertEquals(
        "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=MyBigQueryProject;OAuthType=0;"
            + "OAuthPvtKeyPath=C:\\SecureFiles\\ServiceKeyFile.p12;"
            + "OAuthServiceAcctEmail=bqtest1@data-driver-testing.iam.gserviceaccount.com;",
        actual);
  }

  @Test
  public void testHost() {
    BigQueryParameters param =
        new BigQueryParameters.Builder()
            .setOauthType(0)
            .setProjectId("projectId")
            .setHost("https://www.googleapis.com/auth/cloud-platform")
            .build();

    String actual = param.getHost();

    assertEquals("https://www.googleapis.com/auth/cloud-platform", actual);
  }

  @Test
  public void testPort() {
    BigQueryParameters param =
        new BigQueryParameters.Builder()
            .setOauthType(0)
            .setProjectId("projectId")
            .setPort(1234)
            .build();

    int actual = param.getPort();

    assertEquals(1234, actual);
  }

  @Test
  public void testOauthPvtKeyFile() {
    BigQueryParameters param =
        new BigQueryParameters.Builder()
            .setOauthType(0)
            .setProjectId("projectId")
            .setPort(1234)
            .setOauthPvtKeyFile("C:\\SecureFiles\\ServiceKeyFile.p12")
            .build();

    assertThat(
        param.getDatabaseUrl(), CoreMatchers.containsString("C:\\SecureFiles\\ServiceKeyFile.p12"));
  }

  @Test
  public void testOauthServiceAcctEmail() {
    BigQueryParameters param =
        new BigQueryParameters.Builder()
            .setOauthType(0)
            .setProjectId("projectId")
            .setPort(1234)
            .setOauthServiceAcctEmail("bqtest1@data-driver- testing.iam.gserviceaccount.com")
            .build();

    assertThat(
        param.getDatabaseUrl(),
        CoreMatchers.containsString("bqtest1@data-driver- testing.iam.gserviceaccount.com"));
  }

  @Test
  public void testNoProjectId() {
    exceptionRule.expectMessage("ProjectId is required.");
    exceptionRule.expect(IllegalArgumentException.class);

    new BigQueryParameters.Builder().setOauthType(0).setPort(1234).build();
  }

  @Test
  public void testNoOauthType() {
    exceptionRule.expectMessage("OAuthType is required.");
    exceptionRule.expect(IllegalArgumentException.class);

    new BigQueryParameters.Builder().setProjectId("projectId").build();
  }

  @Test
  public void testNoType() {
    exceptionRule.expect(IllegalArgumentException.class);

    new BigQueryParameters.Builder().setOauthType(0).build();
  }

  @Test
  public void testCategoryIsBigQuery() {
    BigQueryParameters param =
        new BigQueryParameters.Builder().setProjectId("projectId").setOauthType(0).build();

    assertEquals(DatabaseCategory.BIGQUERY, param.getCategory());
  }

  @Test
  public void testDriverIsBigQuery() {
    BigQueryParameters param =
        new BigQueryParameters.Builder().setProjectId("projectId").setOauthType(0).build();

    assertEquals(DatabaseCategory.BIGQUERY.getDriver(), param.getDriver());
  }

  @Test
  public void testToString() {
    BigQueryParameters param =
        new BigQueryParameters.Builder().setProjectId("projectId").setOauthType(0).build();

    System.out.println(param);
    assertEquals(
        "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=projectId;OAuthType=0;",
        param.toString());
  }
}
