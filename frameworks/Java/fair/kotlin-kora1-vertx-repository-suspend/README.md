# Kora 1 Vert.x Suspend Benchmark

TechEmpower `fair-kotlin-kora1-vertx-repository-suspend` implementation.

## Stack

- Framework: Kora `1.2.18`
- HTTP: Kora HTTP Server on Undertow
- Database: Kora `database-vertx` with PostgreSQL Vert.x client
- Controller/repository style: Kotlin `suspend`
- Templates: JTE `3.2.3`
- Runtime: Java 21 container image, Java 25 Gradle runtime

## Commands

```bash
./gradlew :kotlin-kora1-vertx-repository-suspend:compileKotlin
./gradlew :kotlin-kora1-vertx-repository-suspend:testClasses
./gradlew :kotlin-kora1-vertx-repository-suspend:assemble
./gradlew :kotlin-kora1-vertx-repository-suspend:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :kotlin-kora1-vertx-repository-suspend:assemble
docker build -t fair-kotlin-kora1-vertx-repository-suspend kotlin-kora1-vertx-repository-suspend
```

## Configuration

```bash
POSTGRES_VERTX_URI=postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```

Module configuration is in `src/main/resources/application.conf`.

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
