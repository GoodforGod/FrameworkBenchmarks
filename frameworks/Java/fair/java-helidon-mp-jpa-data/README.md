# Helidon MP JPA Data Benchmark

TechEmpower `fair-helidon-jpa-data-nima` implementation.

## Stack

- Framework: Helidon MP `4.5.0`
- HTTP: Helidon MicroProfile JAX-RS
- Database: Helidon Data Repository with Jakarta Persistence, Hibernate ORM, and PostgreSQL JDBC
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-helidon-mp-jpa-data:compileJava
./gradlew :java-helidon-mp-jpa-data:testClasses
./gradlew :java-helidon-mp-jpa-data:distTar
./gradlew :java-helidon-mp-jpa-data:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-helidon-mp-jpa-data:distTar
docker build -t fair-helidon-jpa-data-nima java-helidon-mp-jpa-data
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
VIRTUAL_THREADS_ENABLED=true
```

Module configuration is in `src/main/resources/application.yaml`.
Local Gradle launch tasks derive these values from root `gradle.properties`: `postgresHost`, `postgresPort`,
`postgresDatabase`, `postgresUser`, `postgresPassword`.

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
