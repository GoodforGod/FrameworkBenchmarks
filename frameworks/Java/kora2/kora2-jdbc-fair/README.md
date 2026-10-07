# Kora JDBC Benchmarking Test

This is the `kora-jdbc` implementation of the Java benchmark suite.

It uses Kora HTTP Server on Undertow, Kora JDBC repositories for PostgreSQL, Java 25.

## Implementation Notes

- HTTP server: Undertow via Kora HTTP Server
- Database access: Kora JDBC repositories with PostgreSQL
- Runtime model: Java 25 virtual threads enabled in [`application.conf`](src/main/resources/application.conf)
- Covered tests: plaintext, json, db, queries, updates

## Local Development

Build the distribution:
```bash
./gradlew kora-jdbc:distTar
```

Check PostgreSQL environment values in:
```groovy
run {
    environment([
            "POSTGRES_JDBC_URL": "jdbc:postgresql://localhost:5432/postgres",
            "POSTGRES_USER": "postgres",
            "POSTGRES_PASS": "postgres",
    ])
}
```

Run locally with PostgreSQL environment variables:
```bash
./gradlew kora-jdbc:run
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
