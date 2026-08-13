# Vert.x PgClient Benchmark

TechEmpower `fair-vertx-pg-client` implementation.

## Stack

- Framework: Vert.x `5.0.5`
- HTTP: Vert.x Web
- Database: Vert.x PgClient
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-vertx-pg-client:compileJava
./gradlew :java-vertx-pg-client:testClasses
./gradlew :java-vertx-pg-client:assemble
./gradlew :java-vertx-pg-client:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-vertx-pg-client:assemble
docker build -t fair-vertx-pg-client java-vertx-pg-client
```

## Configuration

```bash
POSTGRES_REACTIVE_URL=postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
POSTGRES_PIPELINING_LIMIT=1
```

`POSTGRES_PIPELINING_LIMIT` defaults to `1`. Other PgClient performance options are hardcoded in `Application`:
prepared statement cache enabled, pool size `512`, and TCP no-delay/keep-alive enabled. Database query/update
loops are executed sequentially.

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
