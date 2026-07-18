# Quarkus JPA Repository Benchmark

This is the Quarkus JPA Repository (Panache) benchmark implementation for the TechEmpower Framework Benchmarks.

## Implementation Details

- **Framework**: Quarkus 3.20.0
- **ORM**: Hibernate ORM with Panache Repository pattern
- **Database**: PostgreSQL via JDBC
- **Template Engine**: jte
- **Java Version**: 25

## Test Endpoints

- `/plaintext` - Returns "Hello, World!" as text/plain
- `/json` - Returns JSON object with message
- `/db` - Single database query using Panache Repository
- `/queries?queries=N` - Multiple database queries using Panache Repository
- `/updates?queries=N` - Multiple database updates using Panache Repository
- `/fortunes` - HTML template rendering with fortunes

## Running the Benchmark

```bash
./gradlew run -DPOSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres \
              -DPOSTGRES_USER=postgres \
              -DPOSTGRES_PASS=postgres
```

## Build

```bash
./gradlew build
```
