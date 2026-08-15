# Kora 1 JDBC Suspend Benchmark

TechEmpower `fair-kotlin-kora1-jdbc-repository-suspend` implementation.

## Stack

- Framework: Kora `1.2.18`
- HTTP: Kora HTTP Server on Undertow
- Database: Kora `database-jdbc` with PostgreSQL JDBC driver
- Controller/repository style: Kotlin `suspend`
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :kotlin-kora1-jdbc-repository-suspend:compileKotlin
./gradlew :kotlin-kora1-jdbc-repository-suspend:testClasses
./gradlew :kotlin-kora1-jdbc-repository-suspend:assemble
./gradlew :kotlin-kora1-jdbc-repository-suspend:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :kotlin-kora1-jdbc-repository-suspend:assemble
docker build -t fair-kotlin-kora1-jdbc-repository-suspend kotlin-kora1-jdbc-repository-suspend
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
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
