# Quarkus JPA Repository Reactive Benchmark

TechEmpower `fair-quarkus-jpa-repository-reactive` implementation.

## Stack

- Framework: Quarkus `3.37.2`
- HTTP: Quarkus REST / JAX-RS on Vert.x
- Database: Hibernate Reactive Panache Repository with Reactive PostgreSQL client
- Reactive type: Mutiny `Uni`
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-quarkus-jpa-repository-reactive:compileJava
./gradlew :java-quarkus-jpa-repository-reactive:testClasses
./gradlew :java-quarkus-jpa-repository-reactive:assemble
./gradlew :java-quarkus-jpa-repository-reactive:quarkusRun
./gradlew :java-quarkus-jpa-repository-reactive:quarkusDev
```

Packaged Quarkus fast-jar:

```bash
java -jar java-quarkus-jpa-repository-reactive/build/quarkus-app/quarkus-run.jar
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-quarkus-jpa-repository-reactive:assemble
docker build -t fair-quarkus-jpa-repository-reactive java-quarkus-jpa-repository-reactive
```

## Configuration

```bash
POSTGRES_REACTIVE_URL=postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```

Module configuration is in `src/main/resources/application.properties`.
Local Gradle launch tasks derive these values from root `gradle.properties`: `postgresHost`, `postgresPort`, `postgresDatabase`, `postgresUser`, `postgresPassword`.

## Benchmark Test URLs

### Plaintext

    http://localhost:8080/plaintext

### JSON

    http://localhost:8080/json

### Single Query

    http://localhost:8080/db

### Multiple Queries

    http://localhost:8080/queries?queries=5

### Data Update

    http://localhost:8080/updates?queries=5

### Fortunes

    http://localhost:8080/fortunes
