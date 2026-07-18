# Spring JDBC Template Benchmark

This is the Spring Boot with JdbcTemplate portion of the Java benchmarking test suite.

## Implementation

This implementation uses:
- Spring Boot 3.5.0
- Spring JDBC (JdbcTemplate)
- PostgreSQL database
- jte templates for server-side rendering
- Jackson for JSON serialization

## Endpoints

- `/plaintext` - Returns "Hello, World!" as text/plain
- `/json` - Returns `{"message":"Hello, World!"}` as application/json
- `/db` - Single database query returning a World object
- `/queries?queries=N` - Multiple database queries (1-500)
- `/updates?queries=N` - Multiple database updates (1-500)
- `/fortunes` - HTML page with sorted fortunes

## Running

```bash
./gradlew bootRun
```

Environment variables:
- `POSTGRES_JDBC_URL` - PostgreSQL JDBC URL
- `POSTGRES_USER` - Database username
- `POSTGRES_PASS` - Database password
