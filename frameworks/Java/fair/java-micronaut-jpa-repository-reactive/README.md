# Micronaut JPA Data Reactive Benchmark

TechEmpower `fair-micronaut-jpa-repository-reactive` implementation.

## Stack

- Framework: Micronaut `5.0.4`
- HTTP: Micronaut HTTP Server Netty
- Database: Micronaut Data Hibernate Reactive with Vert.x PostgreSQL client
- Reactive type: Reactor `Mono` / `Flux`
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-micronaut-jpa-repository-reactive:compileJava
./gradlew :java-micronaut-jpa-repository-reactive:testClasses
./gradlew :java-micronaut-jpa-repository-reactive:distTar
./gradlew :java-micronaut-jpa-repository-reactive:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-micronaut-jpa-repository-reactive:distTar
docker build -t fair-micronaut-jpa-repository-reactive java-micronaut-jpa-repository-reactive
```

## Configuration

```bash
POSTGRES_REACTIVE_URL=postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```

Module configuration is in `src/main/resources/application.yml`.
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
