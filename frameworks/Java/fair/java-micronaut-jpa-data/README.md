# Micronaut JPA Data Benchmark

TechEmpower `fair-micronaut-jpa-data` implementation.

## Stack

- Framework: Micronaut `5.0.4`
- HTTP: Micronaut HTTP Server Netty
- Database: Micronaut Data JPA with Hibernate ORM, PostgreSQL JDBC, and HikariCP
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-micronaut-jpa-data:compileJava
./gradlew :java-micronaut-jpa-data:testClasses
./gradlew :java-micronaut-jpa-data:distTar
./gradlew :java-micronaut-jpa-data:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-micronaut-jpa-data:distTar
docker build -t fair-micronaut-jpa-data java-micronaut-jpa-data
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
