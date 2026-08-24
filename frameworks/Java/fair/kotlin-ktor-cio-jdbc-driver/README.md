# Ktor CIO JDBC Benchmark

TechEmpower `fair-ktor-cio-jdbc-driver` implementation.

## Stack

- Framework: Ktor `3.5.2`
- HTTP: Ktor CIO
- Database: HikariCP over PostgreSQL JDBC driver
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :kotlin-ktor-cio-jdbc-driver:compileKotlin
./gradlew :kotlin-ktor-cio-jdbc-driver:testClasses
./gradlew :kotlin-ktor-cio-jdbc-driver:assemble
./gradlew :kotlin-ktor-cio-jdbc-driver:run
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```
