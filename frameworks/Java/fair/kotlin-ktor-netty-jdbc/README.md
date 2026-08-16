# Ktor Netty JDBC Benchmark

TechEmpower `fair-ktor-netty-jdbc` implementation.

## Stack

- Framework: Ktor `3.5.2`
- HTTP: Ktor Netty
- Database: HikariCP over PostgreSQL JDBC driver
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :kotlin-ktor-netty-jdbc:compileKotlin
./gradlew :kotlin-ktor-netty-jdbc:testClasses
./gradlew :kotlin-ktor-netty-jdbc:assemble
./gradlew :kotlin-ktor-netty-jdbc:run
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```
