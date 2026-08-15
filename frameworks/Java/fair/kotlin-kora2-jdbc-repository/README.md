# Kora 2 JDBC Blocking Benchmark

TechEmpower `fair-kotlin-kora2-jdbc-repository` implementation.

## Stack

- Framework: Kora `2.0.0.RC1`
- HTTP: Kora HTTP Server on Undertow
- Database: Kora `database-jdbc` with PostgreSQL JDBC driver
- Controller/repository style: Kotlin blocking methods
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :kotlin-kora2-jdbc-repository:compileKotlin
./gradlew :kotlin-kora2-jdbc-repository:testClasses
./gradlew :kotlin-kora2-jdbc-repository:assemble
./gradlew :kotlin-kora2-jdbc-repository:run
```

Docker image used by the BlackBox test:

```bash
./gradlew :kotlin-kora2-jdbc-repository:assemble
docker build -t fair-kotlin-kora2-jdbc-repository kotlin-kora2-jdbc-repository
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
