# Quarkus JPA Active Record Benchmark

TechEmpower `fair-quarkus-jpa-ac` implementation.

## Stack

- Framework: Quarkus `3.37.2`
- HTTP: Quarkus REST / JAX-RS on Vert.x
- Database: Hibernate ORM Panache Active Record with PostgreSQL JDBC
- Templates: JTE `3.2.3`
- Runtime: Java 25

## Commands

```bash
./gradlew :java-quarkus-jpa-ac:compileJava
./gradlew :java-quarkus-jpa-ac:testClasses
./gradlew :java-quarkus-jpa-ac:assemble
./gradlew :java-quarkus-jpa-ac:quarkusRun
./gradlew :java-quarkus-jpa-ac:quarkusDev
```

Packaged Quarkus fast-jar:

```bash
java -jar java-quarkus-jpa-ac/build/quarkus-app/quarkus-run.jar
```

Docker image used by the BlackBox test:

```bash
./gradlew :java-quarkus-jpa-ac:assemble
docker build -t fair-quarkus-jpa-ac java-quarkus-jpa-ac
```

## Configuration

```bash
POSTGRES_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
```

Module configuration is in `src/main/resources/application.properties`.
Local Gradle launch tasks derive these values from root `gradle.properties`: `postgresHost`, `postgresPort`,
`postgresDatabase`, `postgresUser`, `postgresPassword`.

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
