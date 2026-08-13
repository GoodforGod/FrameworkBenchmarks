# Fair FrameworkBenchmarks - Java Implementations

This is the Fair portion of the Java [benchmarking test suite](../) comparing a variety of web development platforms.

## Implementations

There are currently 17 implementations:

| Framework                              | Data Access                               | Web Server   | Package                              |
|----------------------------------------|-------------------------------------------|--------------|--------------------------------------|
| `java-kora-jdbc-repository`            | Kora JDBC Repository                      | Undertow     | `io.techempower.benchmark.kora`      |
| `java-helidon-mp-jdbc-client`          | Helidon DbClient + HikariCP               | Helidon MP   | `io.techempower.benchmark.helidon`   |
| `java-helidon-mp-jpa-data`             | Helidon Data Repository + JPA             | Helidon MP   | `io.techempower.benchmark.helidon`   |
| `java-micronaut-jdbc-data`             | Micronaut Data JDBC                       | Netty        | `io.techempower.benchmark.micronaut` |
| `java-micronaut-jpa-data`              | Micronaut Data JPA                        | Netty        | `io.techempower.benchmark.micronaut` |
| `java-micronaut-jpa-data-reactive`     | Micronaut Data Hibernate Reactive         | Netty        | `io.techempower.benchmark.micronaut` |
| `java-micronaut-r2dbc-data-reactive`   | Micronaut Data R2DBC                      | Netty        | `io.techempower.benchmark.micronaut` |
| `java-quarkus-jpa-ac`                  | Hibernate ORM Panache Active Record       | Quarkus REST | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jpa-ac-reactive`         | Hibernate Panache Active Record Reactive  | Quarkus REST | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jpa-repository`          | Hibernate ORM Panache Repository          | Quarkus REST | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jpa-repository-reactive` | Hibernate ORM Panache Repository Reactive | Quarkus REST | `io.techempower.benchmark.quarkus`   |
| `java-spring-jdbc-data`                | Spring Data JDBC                          | Tomcat       | `io.techempower.benchmark.spring`    |
| `java-spring-jdbc-template`            | Spring JdbcTemplate                       | Tomcat       | `io.techempower.benchmark.spring`    |
| `java-spring-jpa-data`                 | Spring Data JPA                           | Tomcat       | `io.techempower.benchmark.spring`    |
| `java-spring-r2dbc-client-reactive`    | Spring DatabaseClient R2DBC               | Netty        | `io.techempower.benchmark.spring`    |
| `java-spring-r2dbc-data-reactive`      | Spring Data R2DBC                         | Netty        | `io.techempower.benchmark.spring`    |
| `java-vertx-pg-client`                 | Vert.x PgClient                           | Vert.x Web   | `io.techempower.benchmark.vertx`     |

## Versions

- [Java 25 (Temurin)](https://adoptium.net/temurin/releases/?version=25)
- [Gradle 9.5.1](https://gradle.org/releases/)
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
