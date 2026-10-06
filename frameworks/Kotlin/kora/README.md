# [Kora](https://github.com/kora-projects/kora)

This is the Kotlin Kora portion of the [benchmarking test suite](../) comparing a variety of web development platforms.

## Implementations

There is currently 1 implementation:

- `kora-jdbc-coroutines` using Kora HTTP Server on Undertow, Kora JDBC repositories with `suspend` methods for PostgreSQL.

## Versions

- [Java 25 (Temurin)](https://adoptium.net/temurin/releases/?version=25)
- [Gradle 9.4.0](https://gradle.org/releases/)
- [Kora 1.2.12](https://github.com/kora-projects/kora)
- [Kotlin 1.9.25](https://kotlinlang.org/docs/releases.html#release-details)

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

