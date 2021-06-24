package ra.db;

import org.junit.Assert;
import org.junit.Test;
import ra.db.parameter.MysqlParameters;

/** Test class. */
public class MysqlParametersTest {

  @Test
  public void testBuildMySqlParam() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder
        .setHost("127.0.0.1")
        .setName("dbName")
        .setPassword("112233")
        .setPort(3306)
        .setUser("user");

    MysqlParameters obj = builder.build();

    Assert.assertNotNull(obj);
  }

  @Test
  public void testBuildMySqlParamUsingProfileSqlTrue() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder
        .setHost("127.0.0.1")
        .setName("dbName")
        .setPassword("112233")
        .setPort(3306)
        .setUser("user")
        .setProfileSql(Boolean.TRUE);

    MysqlParameters obj = builder.build();

    Assert.assertEquals("true", obj.getProperties().getProperty("profileSQL"));
  }
}
