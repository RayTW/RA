# RA
The library provides mysql (CRUD), socket server, logging.

## Download

You can download a jar from GitHub's [releases page](https://github.com/RayTW/RA/releases).

Or use Gradle:

```gradle
repositories {
  mavenCentral()
}

dependencies {
  implementation group: 'org.json', name: 'json', version: '20210307'
  implementation group: 'com.google.code.gson', name: 'gson', version: '2.8.7'
  implementation group: 'mysql', name: 'mysql-connector-java', version: '5.1.48'
  implementation group: 'org.xerial.snappy', name: 'snappy-java', version: '1.1.8.4'
  
  implementation 'io.github.raytw:ra:0.2.0'
}
```

## Author

Ray Li - @raytw on GitHub, Kevin - @tsaibiido on GitHub

## License

MIT. See the [LICENSE](https://raw.githubusercontent.com/RayTW/RA/main/LICENSE) file for details.
