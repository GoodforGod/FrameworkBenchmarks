# Micronaut JPA Data Benchmark

This is the Micronaut JPA Data portion of the Java benchmarking test suite comparing a variety of web development platforms.

## Implementation

This implementation uses:
- Micronaut Framework 4.5.x
- Micronaut Data JPA with Hibernate
- HikariCP connection pool
- PostgreSQL database
- jte template engine for Fortunes test
- Netty server runtime

## Test URLs

### Plaintext Test
```
http://localhost:8080/plaintext
```

### JSON Encoding Test
```
http://localhost:8080/json
```

### Database Query Test
```
http://localhost:8080/db
```

### Database Queries Test
```
http://localhost:8080/queries?queries=5
```

### Database Update Test
```
http://localhost:8080/updates?queries=5
```

### Template Rendering Test
```
http://localhost:8080/fortunes
```

## Versions

- Java 25 (Temurin)
- Gradle 9.4.0
- Micronaut 4.5.3
- Micronaut Data 4.13.0
- Hibernate 6.6.x
- PostgreSQL Driver 42.7.11

## Running

```bash
./gradlew run
```

Environment variables:
- `POSTGRES_JDBC_URL` - JDBC URL for PostgreSQL (default: jdbc:postgresql://localhost:5432/postgres)
- `POSTGRES_USER` - Database username (default: postgres)
- `POSTGRES_PASS` - Database password (default: postgres)
