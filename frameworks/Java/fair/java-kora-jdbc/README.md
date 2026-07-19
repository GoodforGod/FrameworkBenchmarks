# Kora JDBC Benchmark

TechEmpower `fair-kora-jdbc` implementation.

## Stack

- Framework: Kora `2.0.0.alpha6`
- HTTP: Kora HTTP Server on Undertow
- Database: Kora JDBC repository API with PostgreSQL JDBC
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-kora-jdbc:compileJava
./gradlew :java-kora-jdbc:testClasses
./gradlew :java-kora-jdbc:distTar
./gradlew :java-kora-jdbc:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-kora-jdbc:distTar
docker build -t fair-kora-jdbc java-kora-jdbc
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```

Kora-specific configuration is in `src/main/resources/application.conf`.
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
