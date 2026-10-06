# kora-jdbc-coroutines Benchmarking Test

This is the `kora-jdbc-coroutines` implementation of the Kotlin benchmark suite.

It uses Kora HTTP Server on Undertow, Kora JDBC repositories with `suspend` methods for PostgreSQL, Java 25, Kotlin coroutines.

## Implementation Notes

- HTTP server: Undertow via Kora HTTP Server
- Database access: Kora JDBC repositories with `suspend` method signatures
- Runtime model: Kotlin coroutines plus Kora-provided JDBC executor
- Covered tests: plaintext, json, db, queries, updates, fortunes

## Local Development

Build the distribution:
```bash
./gradlew kora-jdbc-coroutines:distTar
```

Check PostgreSQL environment values in:
```kotlin
tasks.run.configure {
    environment(
        mapOf(
            "POSTGRES_JDBC_URL" to "jdbc:postgresql://localhost:5432/postgres",
            "POSTGRES_USER" to "postgres",
            "POSTGRES_PASS" to "postgres"
        )
    )
}
```

Run locally with PostgreSQL environment variables:
```bash
./gradlew kora-jdbc-coroutines:run
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

