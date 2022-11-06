# RA
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.raytw/ra/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.raytw/ra)
[![Build Status](https://travis-ci.com/RayTW/RA.svg?branch=develop)](https://travis-ci.com/RayTW/RA)
[![codecov](https://codecov.io/gh/RayTW/RA/branch/main/graph/badge.svg?token=QVO57XPZRK)](https://codecov.io/gh/RayTW/RA)

The library provides mysql (CRUD), socket server, logging.

## Download

You can download a jar from GitHub's [releases page](https://github.com/RayTW/RA/releases).

Or use Gradle:

```gradle
repositories {
  mavenCentral()
}

dependencies {
  implementation 'io.github.raytw:ra:0.7.0'
}
```

### How to use BigQuery JDBC?
#### Setp 1 
Download the BigQuery [JDBC 4.2-compatible](https://storage.googleapis.com/simba-bq-release/jdbc/SimbaJDBCDriverforGoogleBigQuery42_1.3.0.1001.zip) file.

#### Setp 2
Import the library(GoogleBigQueryJDBC42.jar) to the java build path.

#### Setp 3
```gradle
dependencies {
  implementation 'io.github.raytw:ra:0.7.0'
  implementation 'com.google.cloud:google-cloud-bigquery:2.16.1'
}
```

### How to use Cloud Spanner JDBC?
```gradle
dependencies {
  implementation 'io.github.raytw:ra:0.7.0'
  implementation 'com.google.cloud:google-cloud-spanner-jdbc:2.7.9'
}
```

## API Document 
### [latest](https://raytw.github.io/RA/)

## Example
### Connection to MySQL database
#### Once connection ([OnceConnection](https://raytw.github.io/RA/ra/db/connection/OnceConnection.html))
```java
    MysqlParameters.Builder builder =
        MysqlParameters.newBuilder()
            .setHost("127.0.0.1")
            .setName("test")
            .setPort(3306)
            .setUser("ray")
            .setPassword("raypwd");

    try (DatabaseConnection connection =
        new OnceConnection(builder.build())) { // auto close connection.
      RecordCursor record =
          connection.createStatementExecutor().executeQuery("SELECT * FROM `test_table`");

      record
          .stream()
          .forEach(
              row -> {
                System.out.println("name = " + row.getInt("id") + row.getString("name"));
              });
    } catch (Exception e) {
      e.printStackTrace();
    }
```

#### Keep-alive connection and connection pool. ([DatabaseConnections](https://raytw.github.io/RA/ra/db/DatabaseConnections.html))
```java
    MysqlParameters.Builder builder =
        MysqlParameters.newBuilder()
            .setHost("127.0.0.1")
            .setName("test")
            .setPort(3306)
            .setUser("ray")
            .setPassword("raypwd");

    DatabaseConnections pool = new DatabaseConnections();
    int connectionSize = 5;

    pool.connectOriginalConnection(builder.build(), connectionSize);

    for (int i = 0; i < connectionSize; i++) {
      RecordCursor record = pool.next().executeQuery("SELECT * FROM `test_table` LIMIT 1");

      long id = record.fieldLong("id");
      String name = record.field("name");

      System.out.println("id = " + id + ", name" + name);
    }
```

### Connection to H2 database(in-memory mode)
#### Once connection ([OnceConnection](https://raytw.github.io/RA/ra/db/connection/OnceConnection.html))
```java

    try (OnceConnection connection =
        new OnceConnection(
            new H2Parameters.Builder()
                .inMemory()
                .setName("databaseName")
                .setProperties("DATABASE_TO_UPPER", "false")
                .setProperties("MODE", "MYSQL")
                .build())) {

      connection.connect();

      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `col_int` int(10) UNSIGNED NOT NULL,"
              + "  `col_double` DOUBLE UNSIGNED DEFAULT NULL,"
              + "  `col_boolean` BOOLEAN DEFAULT NULL ,"
              + "  `col_tinyint` tinyint(1) NOT NULL ,"
              + "  `col_enum` enum('default','enum1','enum2') DEFAULT NULL ,"
              + "  `col_decimal` decimal(20,3) DEFAULT 0.000 ,"
              + "  `col_varchar` varchar(50) NOT NULL ,"
              + "  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),"
              + "  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() "
              + "ON UPDATE current_timestamp()"
              + ");";

      executor.executeUpdate(createTableSql);

      String sql =
          "INSERT INTO test_table SET col_int=1"
              + ",col_double=1.01"
              + ",col_boolean=true"
              + ",col_tinyint=5"
              + ",col_enum='enum1'"
              + ",col_decimal=1.1111"
              + ",col_varchar='col_varchar'"
              + ",created_at=NOW();";

      executor.executeUpdate(sql);

      RecordCursor record = executor.executeQuery("SELECT * FROM `test_table`");

      record
          .stream()
          .forEach(
              row -> {
                System.out.println("col_int=" + row.getInt("col_int"));
              });

      executor.executeUpdate("DROP TABLE test_table");
    }
```

### The database uses the prepare statement.

```java
    try (OnceConnection connection =
        new OnceConnection(
            new H2Parameters.Builder()
                .inMemory()
                .setName("databaseName")
                .setProperties("DATABASE_TO_UPPER", "false")
                .setProperties("MODE", "MYSQL")
                .build())) {

      connection.connect();

      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `age` int(10) UNSIGNED NOT NULL,"
              + "  `name` VARCHAR(100) DEFAULT NULL"
              + ");";

      executor.executeUpdate(createTableSql);

      String sql = "INSERT INTO test_table SET age=?" + ",name=?;";

      executor.prepareExecuteUpdate(
          Prepared.newBuilder(sql)
              .set(1, ParameterValue.int64(18))
              .set(2, ParameterValue.string("ray"))
              .build());

      executor.prepareExecuteUpdate(
          Prepared.newBuilder(sql)
              .set(1, ParameterValue.int64(22))
              .set(2, ParameterValue.string("name test"))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT * FROM `test_table` WHERE name = ? LIMIT 1;")
                  .set(1, ParameterValue.string("ray"))
                  .build());

      System.out.println("name=" + record.field("name") + ", age=" + record.fieldInt("age"));

      executor.executeUpdate("DROP TABLE test_table");
    }
```

## Author

Ray Li - @raytw on GitHub, Kevin - @tsaibiido on GitHub

## License

MIT. See the [LICENSE](https://raw.githubusercontent.com/RayTW/RA/main/LICENSE) file for details.
