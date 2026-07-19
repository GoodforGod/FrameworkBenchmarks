# Spring R2DBC Data Reactive Benchmark

TechEmpower `fair-spring-r2dbc-data-reactive` implementation.

## Stack

- Framework: Spring Boot `4.1.0`
- HTTP: Spring WebFlux on Netty
- Database: Spring Data R2DBC with PostgreSQL R2DBC driver
- Reactive type: Reactor `Mono` / `Flux`
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-spring-r2dbc-data-reactive:compileJava
./gradlew :java-spring-r2dbc-data-reactive:testClasses
./gradlew :java-spring-r2dbc-data-reactive:bootJar
./gradlew :java-spring-r2dbc-data-reactive:bootRun
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-spring-r2dbc-data-reactive:bootJar
docker build -t fair-spring-r2dbc-data-reactive java-spring-r2dbc-data-reactive
```

## Configuration

```bash
POSTGRES_R2DBC_URL=r2dbc:postgresql://localhost:5432/postgres
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
