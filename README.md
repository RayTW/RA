# RA
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.raytw/ra/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.raytw/ra)
[![Build Status](https://travis-ci.com/RayTW/RA.svg?branch=develop)](https://travis-ci.com/RayTW/RA)
[![codecov](https://codecov.io/gh/RayTW/RA/branch/develop/graph/badge.svg?token=QVO57XPZRK)](https://codecov.io/gh/RayTW/RA)

The library provides mysql (CRUD), socket server, logging.

## Download

You can download a jar from GitHub's [releases page](https://github.com/RayTW/RA/releases).

Or use Gradle:

```gradle
repositories {
  mavenCentral()
}

dependencies {
  implementation 'io.github.raytw:ra:0.3.0'
}
```

## API Document 
### [latest](https://raytw.github.io/RA/)

## Example
### Connection to MySQL database
#### Once connection ([OnceConnection](https://raytw.github.io/RA/ra/db/connection/OnceConnection.html))
```java
  MysqlParameters.Builder builder = new MysqlParameters.Builder();

  builder.setHost("127.0.0.1").setName("test").setPort(3306).setUser("ray").setPassword("raypwd");

  DatabaseConnection connection = new OnceConnection(builder.build());

  if (!connection.connect()) {
    return;
  }

  RecordCursor record =
    connection.createStatementExecutor().executeQuery("SELECT * FROM `test_table`");

  record
    .stream()
    .forEach(
      row -> {
        System.out.println("name = " + row.getInt("id") + row.getString("name"));
      });

  // Require to close when uses once connection.
  try {
    connection.close();
  } catch (Exception e) {
    e.printStackTrace();
  }
```

#### Keep-alive connection and connection pool. ([DatabaseConnections](https://raytw.github.io/RA/ra/db/DatabaseConnections.html))
```java
  MysqlParameters.Builder builder = new MysqlParameters.Builder();

  builder.setHost("127.0.0.1").setName("test").setPort(3306).setUser("ray").setPassword("raypwd");

  DatabaseConnections pool = new DatabaseConnections();
  int connectionSize = 5;

  pool.connectOriginalConnection(builder.build(), connectionSize);

  for (int i = 0; i < connectionSize; i++) {
    RecordCursor record = pool.next().executeQuery("SELECT * FROM `test_table`");

    record
      .stream()
      .forEach(
        row -> {
        System.out.println("name = " + row.getInt("id") + row.getString("name"));
        });
  }
```

## Author

Ray Li - @raytw on GitHub, Kevin - @tsaibiido on GitHub

## License

MIT. See the [LICENSE](https://raw.githubusercontent.com/RayTW/RA/main/LICENSE) file for details.
