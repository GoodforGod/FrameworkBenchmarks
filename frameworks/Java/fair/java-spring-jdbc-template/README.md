# Spring JdbcTemplate Benchmark

TechEmpower `fair-spring-jdbc-template` implementation.

## Stack

- Framework: Spring Boot `4.1.0`
- HTTP: Spring MVC on Tomcat
- Database: Spring `JdbcTemplate` with PostgreSQL JDBC
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-spring-jdbc-template:compileJava
./gradlew :java-spring-jdbc-template:testClasses
./gradlew :java-spring-jdbc-template:distTar
./gradlew :java-spring-jdbc-template:bootRun
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-spring-jdbc-template:distTar
docker build -t fair-spring-jdbc-template java-spring-jdbc-template
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```

Module configuration is in `src/main/resources/application.yml`.
Local Gradle launch tasks derive these values from root `gradle.properties`: `postgresHost`, `postgresPort`, `postgresDatabase`, `postgresUser`, `postgresPassword`.

## Benchmark Test URLs

### Plaintext

    http://localhost:8080/plaintext

### JSON

    http://localhost:8080/json

### Single Query

    http://localhost:8080/db

### Multiple Queries

    http://localhost:8080/queries?queries=5

### Data Update

    http://localhost:8080/updates?queries=5

### Fortunes

    http://localhost:8080/fortunes
