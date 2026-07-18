# Micronaut JDBC Benchmark

This is the Micronaut JDBC portion of the Java benchmarking test suite comparing a variety of web development platforms.

## Implementation

This implementation uses Micronaut Framework with JDBC Data repositories.

## Tech Stack

- [Java 25 (Temurin)](https://adoptium.net/temurin/releases/?version=25)
- [Gradle 9.4.0](https://gradle.org/releases/)
- [Micronaut 4.5.0](https://micronaut.io/)
- [Micronaut Data JDBC](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [PostgreSQL Driver](https://jdbc.postgresql.org/)
- [JTE 3.2.3](https://jte.gg/)

## Test URLs

### Plaintext Test

    http://localhost:8080/plaintext

### JSON Encoding Test

    http://localhost:8080/json

### Database Query Test

    http://localhost:8080/db

### Database Queries Test

    http://localhost:8080/queries?queries=5

### Database Update Test

    http://localhost:8080/updates?queries=5

### Template Rendering Test

    http://localhost:8080/fortunes

## Running

```bash
export POSTGRES_JDBC_URL="jdbc:postgresql://localhost:5432/postgres"
export POSTGRES_USER="postgres"
export POSTGRES_PASS="postgres"
./gradlew run
```
