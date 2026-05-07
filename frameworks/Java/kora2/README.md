# [Kora Benchmarking Test](https://github.com/kora-projects/kora)

This is the Kora portion of the Java [benchmarking test suite](../) comparing a variety of web development platforms.

## Implementations

There is currently 1 implementation:

- `kora-jdbc` using Kora HTTP Server on Undertow and Kora JDBC repositories.

## Versions

- [Java 25 (Temurin)](https://adoptium.net/temurin/releases/?version=25)
- [Gradle 9.4.0](https://gradle.org/releases/)
- [Kora 2.0.0.alpha6](https://github.com/kora-projects/kora)

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
