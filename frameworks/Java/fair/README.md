# Fair FrameworkBenchmarks - Java Implementations

This is the Fair portion of the Java [benchmarking test suite](../) comparing a variety of web development platforms.

## Implementations

There are currently 10 implementations:

| Framework                         | ORM                 | Web Server   | Package                                    |
|-----------------------------------|---------------------|--------------|--------------------------------------------|
| `java-kora-jdbc`                  | Kora JDBC           | Undertow     | io.koraframework.benchmark                 |
| `java-helidon-jdbc-nima`          | JDBC + HikariCP     | Helidon NIMA | io.helidon.benchmark.nima                  |
| `java-micronaut-jdbc-data`        | Micronaut Data JDBC | Netty        | io.micronaut.benchmark                     |
| `java-micronaut-jpa-data`         | JPA/Hibernate       | Netty        | io.micronaut.benchmark.jpa                 |
| `java-quarkus-jpa-dao`            | Panache DAO         | Undertow     | io.quarkus.benchmark.dao                   |
| `java-quarkus-jpa-repository`     | Panache Repository  | Undertow     | io.quarkus.benchmark.repository            |
| `java-spring-jdbc-data`           | Spring Data JDBC    | Tomcat       | org.springframework.benchmark.jdbc         |
| `java-spring-jdbc-template`       | JdbcTemplate        | Tomcat       | org.springframework.benchmark.jdbctemplate |
| `java-spring-jpa-data`            | Spring Data JPA     | Tomcat       | io.spring.benchmark                        |
| `java-spring-r2dbc-data-reactive` | R2DBC (Reactive)    | Netty        | io.spring.benchmark.r2dbc                  |

## Versions

- [Java 25 (Temurin)](https://adoptium.net/temurin/releases/?version=25)
- [Gradle 9.4.0](https://gradle.org/releases/)
- Framework versions vary by implementation

## Test URLs

### Plaintext Test

    http://localhost:8080/plaintext

### JSON Encoding Test

    http://localhost:8080/json

### Database Query Test

    http://localhost:8080/db

### Database Queries Test

    http://localhost:8080/queries?queries=5

### Database Update Test

    http://localhost:8080/updates?queries=5

### Template Rendering Test

    http://localhost:8080/fortunes
