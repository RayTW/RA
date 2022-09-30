package test.mock;

import ra.db.DatabaseCategory;
import ra.db.parameter.Accountable;
import ra.db.parameter.DatabaseParameters;

/** Test class. */
public class MockDatabaseParameters implements DatabaseParameters, Accountable {

  @Override
  public DatabaseCategory getCategory() {

    return null;
  }

  @Override
  public String getUrlSchema() {

    return null;
  }

  @Override
  public String getDriver() {

    return null;
  }

  @Override
  public String getHost() {

    return null;
  }

  @Override
  public int getPort() {

    return 0;
  }

  @Override
  public String getUser() {

    return null;
  }

  @Override
  public String getPassword() {

    return null;
  }

  @Override
  public String getDatabaseUrl() {

    return null;
  }
}
