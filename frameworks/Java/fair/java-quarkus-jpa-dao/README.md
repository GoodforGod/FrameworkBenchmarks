# Quarkus JPA DAO Benchmark

This is the Quarkus portion of the Java benchmarking test suite using JPA with Panache DAO pattern.

## Implementation Details

- **Framework**: Quarkus 3.21.0
- **ORM**: Hibernate ORM with Panache DAO
- **Database**: PostgreSQL
- **Web**: JAX-RS (RESTEasy Reactive)
- **JSON**: Jackson
- **Template**: jte (for fortunes)
- **Java**: 25

## Test URLs

- Plaintext: http://localhost:8080/plaintext
- JSON: http://localhost:8080/json
- Single Query: http://localhost:8080/db
- Multiple Queries: http://localhost:8080/queries?queries=5
- Updates: http://localhost:8080/updates?queries=5
- Fortunes: http://localhost:8080/fortunes

## Running

```bash
./gradlew run -DPOSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres -DPOSTGRES_USER=postgres -DPOSTGRES_PASS=postgres
```

Or build and run the native image:

```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```
