# Spring WebFlux R2DBC Reactive Benchmarking Test

This is the `java-spring-r2dbc-data-reactive` implementation of the Java benchmark suite.

It uses Spring Boot WebFlux, Spring Data R2DBC for reactive PostgreSQL access, Java 25.

## Implementation Notes

- HTTP server: Spring WebFlux (reactive, non-blocking)
- Database access: Spring Data R2DBC with PostgreSQL R2DBC driver
- Runtime model: Fully reactive with Project Reactor (Mono/Flux)
- Covered tests: plaintext, json, db, queries, updates, fortunes

## Local Development

Build the distribution:
```bash
./gradlew java-spring-r2dbc-data-reactive:bootJar
```

Run locally with PostgreSQL environment variables:
```bash
export POSTGRES_R2DBC_URL="r2dbc:postgresql://localhost:5432/postgres"
export POSTGRES_USER="postgres"
export POSTGRES_PASS="postgres"
./gradlew java-spring-r2dbc-data-reactive:bootRun
```

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
