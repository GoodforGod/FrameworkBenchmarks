# Ktor CIO JDBC Benchmark

TechEmpower `fair-ktor-cio-jdbc` implementation.

## Stack

- Framework: Ktor `3.3.3`
- HTTP: Ktor CIO
- Database: HikariCP over PostgreSQL JDBC driver
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :kotlin-ktor-cio-jdbc:compileKotlin
./gradlew :kotlin-ktor-cio-jdbc:testClasses
./gradlew :kotlin-ktor-cio-jdbc:assemble
./gradlew :kotlin-ktor-cio-jdbc:run
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```
