# Fair FrameworkBenchmarks - Java and Kotlin Implementations

This is the Fair portion of the Java and Kotlin [benchmarking test suite](../) comparing a variety of web development platforms.

## Implementations

There are currently 24 implementations:

| Framework                                  | Data Access                               | Web Server | Package                              |
|--------------------------------------------|-------------------------------------------|------------|--------------------------------------|
| `kotlin-kora1-jdbc-repository-suspend`     | Kora 1 JDBC Repository with suspend API   | Undertow   | `io.techempower.benchmark.kora`      |
| `kotlin-kora1-vertx-repository-suspend`    | Kora 1 Vert.x Repository with suspend API | Undertow   | `io.techempower.benchmark.kora`      |
| `kotlin-kora2-jdbc-repository`             | Kora 2 JDBC Repository with blocking API  | Undertow   | `io.techempower.benchmark.kora`      |
| `java-kora1-jdbc-repository`               | Kora 1 JDBC Repository                    | Undertow   | `io.techempower.benchmark.kora`      |
| `java-kora2-jdbc-repository`               | Kora 2 JDBC Repository                    | Undertow   | `io.techempower.benchmark.kora`      |
| `kotlin-ktor-cio-jdbc`                     | HikariCP + PostgreSQL JDBC                | CIO        | `io.techempower.benchmark.ktor`      |
| `kotlin-ktor-netty-jdbc`                   | HikariCP + PostgreSQL JDBC                | Netty      | `io.techempower.benchmark.ktor`      |
| `java-helidon-mp-jdbc-client`              | Helidon DbClient + HikariCP               | Nima       | `io.techempower.benchmark.helidon`   |
| `java-helidon-mp-jpa-repository`           | Helidon Data Repository + JPA             | Nima       | `io.techempower.benchmark.helidon`   |
| `java-micronaut-jdbc-repository`           | Micronaut Data JDBC                       | Netty      | `io.techempower.benchmark.micronaut` |
| `java-micronaut-jpa-repository`            | Micronaut Data JPA                        | Netty      | `io.techempower.benchmark.micronaut` |
| `java-micronaut-jpa-repository-reactive`   | Micronaut Data Hibernate Reactive         | Netty      | `io.techempower.benchmark.micronaut` |
| `java-micronaut-r2dbc-repository-reactive` | Micronaut Data R2DBC                      | Netty      | `io.techempower.benchmark.micronaut` |
| `java-quarkus-jpa-ac`                      | Hibernate ORM Panache Active Record       | Vert.x Web | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jpa-ac-reactive`             | Hibernate Panache Active Record Reactive  | Vert.x Web | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jooq`                        | jOOQ DSL over Quarkus JDBC                | Vert.x Web | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jpa-repository`              | Hibernate ORM Panache Repository          | Vert.x Web | `io.techempower.benchmark.quarkus`   |
| `java-quarkus-jpa-repository-reactive`     | Hibernate ORM Panache Repository Reactive | Vert.x Web | `io.techempower.benchmark.quarkus`   |
| `java-spring-jdbc-repository`              | Spring Data JDBC                          | Tomcat     | `io.techempower.benchmark.spring`    |
| `java-spring-jdbc-template`                | Spring JdbcTemplate                       | Tomcat     | `io.techempower.benchmark.spring`    |
| `java-spring-jpa-data`                     | Spring Data JPA                           | Tomcat     | `io.techempower.benchmark.spring`    |
| `java-spring-r2dbc-client-reactive`        | Spring DatabaseClient R2DBC               | Netty      | `io.techempower.benchmark.spring`    |
| `java-spring-r2dbc-repository-reactive`    | Spring Data R2DBC                         | Netty      | `io.techempower.benchmark.spring`    |
| `java-vertx-pg-client`                     | Vert.x PgClient                           | Vert.x Web | `io.techempower.benchmark.vertx`     |

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
