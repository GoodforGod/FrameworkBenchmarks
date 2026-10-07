# Fair Framework Benchmarks

**English** | [Русский](README.ru.md)

A set of 26 TechEmpower-style candidates built to be compared **like for like**: the same endpoints, the same
SQL, the same JVM flags and container image, and code written the way each framework's own documentation
recommends, without benchmark-only tricks. The suite covers Kora 1 and 2, Quarkus, Micronaut, Spring Boot,
Helidon, Ktor and Vert.x on the JVM, plus one Rust (ntex) candidate as a non-JVM reference.

- [What "fair" means here](#what-fair-means-here)
- [Scenario](#scenario)
- [Environment](#environment)
- [Candidates](#candidates)
- [Results](#results)
- [How to run](#how-to-run)
- [Known differences between candidates](#known-differences-between-candidates)

## What "fair" means here

| Rule                             | How it is applied                                                                                                                                           |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Same work per request            | `/db` runs exactly one `SELECT id, randomnumber FROM world WHERE id = ?` per request. No result caching, no query batching across requests                  |
| Idiomatic code                   | Each candidate uses the framework's documented way of writing a controller and a repository: declarative repositories, JPA, R2DBC, `JdbcTemplate` and so on |
| Same runtime                     | Every JVM candidate runs on `eclipse-temurin:25-jre-jammy` with identical `JAVA_OPTS`                                                                       |
| Same driver settings             | PostgreSQL JDBC `42.7.13` with server-side prepared statements (`prepareThreshold=1`) for all JDBC candidates                                               |
| Blocking code off the event loop | Blocking JDBC/JPA endpoints run on virtual threads or a blocking dispatcher, never directly on an I/O thread                                                |
| No pipelining tricks             | Each request waits for its own query round trip                                                                                                             |

Verified for the candidates that were profiled (Kora 2, Quarkus reactive, Micronaut JDBC): one statement per
request on the Postgres side, one Bind/Execute/Sync exchange, two network packets per query.

## Scenario

| Parameter            | Value                                                                                                  |
|----------------------|--------------------------------------------------------------------------------------------------------|
| Test                 | TechEmpower `db` (single database query), `GET /db`                                                    |
| Load generator       | `wrk` from the TFB toolset, 24 threads, keep-alive                                                     |
| Concurrency          | 256 connections                                                                                        |
| Phases per candidate | primer (5 s, 8 connections) → warmup (30 s, 256 connections) → measurement (**30 s**, 256 connections) |
| Rounds               | 1 per candidate; candidates run one after another, each in a fresh container                           |
| Database             | external PostgreSQL on a separate machine (`./tfb-ext`), table `world` with 10,000 rows                |
| Metric               | requests per second = total requests ÷ 30 s; latency as reported by wrk                                |

The other TFB endpoints (`/plaintext`, `/json`, `/queries`, `/updates`, `/fortunes`) are implemented by every
candidate, but only `/db` is measured in the published run.

## Environment

|                     | Application + load generator host                                                     | Database host             |
|---------------------|---------------------------------------------------------------------------------------|---------------------------|
| CPU                 | 12 cores / 24 threads                                                                 | 8 cores / 16 threads      |
| Memory              | 32 GB                                                                                 | 32 GB                     |
| Software            | Docker (Linux containers); application container and `wrk` container on the same host | PostgreSQL 18.6 in Docker |
| CPU / memory limits | none: the application container sees all 24 threads                                   | none                      |

- **Network:** the two hosts are connected directly with a 2.5 Gbit/s Ethernet cable. A single query round
  trip takes about 0.15 ms when idle and about 1.5 ms at 256 connections.
- **PostgreSQL settings:** `max_connections=2000`, `synchronous_commit=off`, `shared_buffers=256MB`,
  `pg_stat_statements` enabled.
- **JVM flags (all JVM candidates):** `-XX:+UseParallelGC -XX:+UseNUMA -XX:+UseCompactObjectHeaders
  -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=64m -XX:MaxInlineLevel=20
  -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false`. No heap size is set, so the JVM default applies
  (a quarter of host memory).
- **Shared host caveat:** `wrk` and the application compete for the same 24 CPU threads.

At this load the database host is close to saturation for the fastest candidates, so the top of the table is
bounded by PostgreSQL rather than by the frameworks.

## Candidates

Framework versions: Kora `1.2.22` / `2.0.0.RC2`, Quarkus `3.40.1`, Micronaut `5.2.1`, Spring Boot `4.1.1`,
Helidon `4.5.5`, Ktor `3.6.0`, Vert.x `5.2.0`, Hibernate ORM `7.4.5`, jOOQ `3.21.9`, HikariCP `7.1.0`,
Kotlin `2.4.20`, ntex `3.12.3`.

"Where `/db` runs" is the thread that executes the controller method and the query.

### Kora

| TFB test                              | Module                                                                         | HTTP server | Data access                             | Where `/db` runs                                                                   | Max DB connections |
|---------------------------------------|--------------------------------------------------------------------------------|-------------|-----------------------------------------|------------------------------------------------------------------------------------|-------------------:|
| `fair-kora2-jdbc-repository`          | [java-kora2-jdbc-repository](java-kora2-jdbc-repository)                       | Undertow    | Kora JDBC repository                    | one virtual thread per HTTP connection; response sent from the Undertow I/O thread |                128 |
| `fair-kora2-jdbc-repository-kotlin`   | [kotlin-kora2-jdbc-repository](kotlin-kora2-jdbc-repository)                   | Undertow    | Kora JDBC repository (Kotlin)           | same as above                                                                      |                128 |
| `fair-kora1-jdbc-repository`          | [java-kora1-jdbc-repository](java-kora1-jdbc-repository)                       | Undertow    | Kora 1 JDBC repository                  | platform-thread worker pool                                                        |                256 |
| `fair-kora1-jdbc-repository-vt`       | [java-kora1-jdbc-repository](java-kora1-jdbc-repository)                       | Undertow    | Kora 1 JDBC repository                  | virtual threads (`VIRTUAL_THREADS_ENABLED=true`)                                   |                256 |
| `fair-kora1-jdbc-repository-suspend`  | [kotlin-kora1-jdbc-repository-suspend](kotlin-kora1-jdbc-repository-suspend)   | Undertow    | Kora 1 JDBC repository, `suspend` API   | coroutine; JDBC on `Dispatchers.IO`                                                |                256 |
| `fair-kora1-vertx-repository-suspend` | [kotlin-kora1-vertx-repository-suspend](kotlin-kora1-vertx-repository-suspend) | Undertow    | Kora 1 Vert.x repository, `suspend` API | coroutine; non-blocking Vert.x PostgreSQL client                                   |                256 |

The Kora 2 candidates set `httpServer.undertow.ioThreads = 12` and
`-Djdk.virtualThreadScheduler.parallelism=12`: half of the 24 CPU threads for Undertow I/O, half for
virtual-thread carriers.

### Quarkus

| TFB test                               | Module                                                                       | HTTP server           | Data access                               | Where `/db` runs                                   | Max DB connections |
|----------------------------------------|------------------------------------------------------------------------------|-----------------------|-------------------------------------------|----------------------------------------------------|-------------------:|
| `fair-quarkus-jooq`                    | [java-quarkus-jooq](java-quarkus-jooq)                                       | Vert.x (Quarkus REST) | jOOQ DSL over JDBC                        | virtual thread per request (`@RunOnVirtualThread`) |                256 |
| `fair-quarkus-jpa-ac`                  | [java-quarkus-jpa-ac](java-quarkus-jpa-ac)                                   | Vert.x (Quarkus REST) | Hibernate ORM Panache, active record      | virtual thread per request                         |                256 |
| `fair-quarkus-jpa-repository`          | [java-quarkus-jpa-repository](java-quarkus-jpa-repository)                   | Vert.x (Quarkus REST) | Hibernate ORM Panache, repository         | virtual thread per request                         |                256 |
| `fair-quarkus-jpa-ac-reactive`         | [java-quarkus-jpa-ac-reactive](java-quarkus-jpa-ac-reactive)                 | Vert.x (Quarkus REST) | Hibernate Reactive Panache, active record | Vert.x event loop                                  |                256 |
| `fair-quarkus-jpa-repository-reactive` | [java-quarkus-jpa-repository-reactive](java-quarkus-jpa-repository-reactive) | Vert.x (Quarkus REST) | Hibernate Reactive Panache, repository    | Vert.x event loop                                  |                256 |

### Micronaut

| TFB test                                   | Module                                                                               | HTTP server | Data access                        | Where `/db` runs                                    | Max DB connections |
|--------------------------------------------|--------------------------------------------------------------------------------------|-------------|------------------------------------|-----------------------------------------------------|-------------------:|
| `fair-micronaut-jdbc-repository`           | [java-micronaut-jdbc-repository](java-micronaut-jdbc-repository)                     | Netty       | Micronaut Data JDBC                | virtual thread per request (`@ExecuteOn(BLOCKING)`) |                256 |
| `fair-micronaut-jpa-repository`            | [java-micronaut-jpa-repository](java-micronaut-jpa-repository)                       | Netty       | Micronaut Data JPA (Hibernate ORM) | virtual thread per request (`@ExecuteOn(BLOCKING)`) |                256 |
| `fair-micronaut-jpa-repository-reactive`   | [java-micronaut-jpa-repository-reactive](java-micronaut-jpa-repository-reactive)     | Netty       | Micronaut Data Hibernate Reactive  | Netty event loop                                    |                256 |
| `fair-micronaut-r2dbc-repository-reactive` | [java-micronaut-r2dbc-repository-reactive](java-micronaut-r2dbc-repository-reactive) | Netty       | Micronaut Data R2DBC               | Netty event loop                                    |                256 |

### Spring Boot

| TFB test                                | Module                                                                         | HTTP server   | Data access                     | Where `/db` runs           | Max DB connections |
|-----------------------------------------|--------------------------------------------------------------------------------|---------------|---------------------------------|----------------------------|-------------------:|
| `fair-spring-jdbc-template`             | [java-spring-jdbc-template](java-spring-jdbc-template)                         | Tomcat        | `JdbcTemplate`                  | virtual thread per request |                256 |
| `fair-spring-jdbc-repository`           | [java-spring-jdbc-repository](java-spring-jdbc-repository)                     | Tomcat        | Spring Data JDBC                | virtual thread per request |                256 |
| `fair-spring-jpa-repository`            | [java-spring-jpa-repository](java-spring-jpa-repository)                       | Tomcat        | Spring Data JPA (Hibernate ORM) | virtual thread per request |                256 |
| `fair-spring-r2dbc-client-reactive`     | [java-spring-r2dbc-client-reactive](java-spring-r2dbc-client-reactive)         | Reactor Netty | `DatabaseClient` (R2DBC)        | Netty event loop           |                256 |
| `fair-spring-r2dbc-repository-reactive` | [java-spring-r2dbc-repository-reactive](java-spring-r2dbc-repository-reactive) | Reactor Netty | Spring Data R2DBC               | Netty event loop           |                256 |

### Helidon, Ktor, Vert.x, Rust

| TFB test                         | Module                                                           | HTTP server       | Data access                                  | Where `/db` runs                               | Max DB connections |
|----------------------------------|------------------------------------------------------------------|-------------------|----------------------------------------------|------------------------------------------------|-------------------:|
| `fair-helidon-mp-jdbc-client`    | [java-helidon-mp-jdbc-client](java-helidon-mp-jdbc-client)       | Helidon WebServer | Helidon DbClient (JDBC)                      | virtual thread per request                     |                256 |
| `fair-helidon-mp-jpa-repository` | [java-helidon-mp-jpa-repository](java-helidon-mp-jpa-repository) | Helidon WebServer | Helidon Data repository (JPA)                | virtual thread per request                     |                256 |
| `fair-ktor-netty-jdbc-driver`    | [kotlin-ktor-netty-jdbc-driver](kotlin-ktor-netty-jdbc-driver)   | Netty             | HikariCP + plain JDBC                        | coroutine; JDBC on `Dispatchers.IO`            |                256 |
| `fair-ktor-cio-jdbc-driver`      | [kotlin-ktor-cio-jdbc-driver](kotlin-ktor-cio-jdbc-driver)       | Ktor CIO          | HikariCP + plain JDBC                        | coroutine; JDBC on `Dispatchers.IO`            |                256 |
| `fair-vertx-pg-client`           | [java-vertx-pg-client](java-vertx-pg-client)                     | Vert.x Web        | Vert.x PostgreSQL client, pipelining limit 1 | Vert.x event loop, one verticle per CPU thread |                256 |
| `fair-ntex-db-tokio`             | [rust-ntex-db-tokio](rust-ntex-db-tokio)                         | ntex              | `tokio-postgres`                             | async task on a per-CPU worker thread          | 8 per worker (192) |

Every module has its own README with build commands and configuration.

## Results

Run of 2026-10-06, `db` test, 256 connections, 30 s measurement, one round. No candidate returned errors.
Source: [`results.json`](../../../results.json) in the repository root.

|  # | Candidate                                  | Requests/s | Latency avg | Latency stdev | Latency max |
|---:|--------------------------------------------|-----------:|------------:|--------------:|------------:|
|  1 | `fair-kora2-jdbc-repository`               |    134,313 |     7.20 ms |      46.02 ms |   609.87 ms |
|  2 | `fair-vertx-pg-client`                     |    133,788 |     8.00 ms |      50.85 ms |   660.33 ms |
|  3 | `fair-kora2-jdbc-repository-kotlin`        |    132,482 |     8.14 ms |      52.22 ms |   674.86 ms |
|  4 | `fair-quarkus-jpa-repository`              |    132,217 |     6.14 ms |      38.61 ms |   550.97 ms |
|  5 | `fair-ntex-db-tokio`                       |    131,192 |     7.33 ms |      46.80 ms |   648.97 ms |
|  6 | `fair-quarkus-jpa-ac-reactive`             |    130,189 |     6.32 ms |      38.38 ms |   549.80 ms |
|  7 | `fair-quarkus-jpa-ac`                      |    129,913 |     7.53 ms |      47.66 ms |   636.20 ms |
|  8 | `fair-quarkus-jpa-repository-reactive`     |    129,628 |     6.74 ms |      41.69 ms |   586.20 ms |
|  9 | `fair-micronaut-jdbc-repository`           |    126,328 |     4.78 ms |      28.29 ms |   444.94 ms |
| 10 | `fair-quarkus-jooq`                        |    126,234 |     2.03 ms |       1.53 ms |    66.41 ms |
| 11 | `fair-kora1-jdbc-repository`               |    122,413 |     5.63 ms |      32.25 ms |   515.70 ms |
| 12 | `fair-spring-jdbc-template`                |    120,594 |     6.10 ms |      37.73 ms |   537.06 ms |
| 13 | `fair-ktor-netty-jdbc-driver`              |    112,364 |     5.21 ms |      29.68 ms |   459.73 ms |
| 14 | `fair-kora1-vertx-repository-suspend`      |    110,486 |     5.68 ms |      32.41 ms |   504.91 ms |
| 15 | `fair-kora1-jdbc-repository-suspend`       |    108,141 |     5.29 ms |      30.04 ms |   457.11 ms |
| 16 | `fair-helidon-mp-jdbc-client`              |    102,580 |    11.14 ms |      66.90 ms |   840.04 ms |
| 17 | `fair-kora1-jdbc-repository-vt`            |     92,195 |     5.53 ms |      28.22 ms |   456.80 ms |
| 18 | `fair-ktor-cio-jdbc-driver`                |     90,311 |     2.72 ms |       1.13 ms |    29.10 ms |
| 19 | `fair-micronaut-r2dbc-repository-reactive` |     64,880 |     8.97 ms |      43.43 ms |   618.98 ms |
| 20 | `fair-spring-r2dbc-repository-reactive`    |     63,799 |    10.15 ms |      50.79 ms |   677.52 ms |
| 21 | `fair-spring-r2dbc-client-reactive`        |     63,606 |    11.22 ms |      57.62 ms |   850.33 ms |
| 22 | `fair-spring-jdbc-repository`              |     51,860 |    11.28 ms |      52.56 ms |   661.68 ms |
| 23 | `fair-spring-jpa-repository`               |     51,616 |    10.81 ms |      50.13 ms |   641.49 ms |
| 24 | `fair-micronaut-jpa-repository-reactive`   |     46,800 |     8.77 ms |      32.12 ms |   481.46 ms |
| 25 | `fair-helidon-mp-jpa-repository`           |     46,571 |    13.13 ms |      61.65 ms |   765.62 ms |
| 26 | `fair-micronaut-jpa-repository`            |     18,672 |    16.05 ms |      30.38 ms |   483.73 ms |

Requests per second, `db` test at 256 connections (longer is better):

```text
kora2-jdbc-repository               ██████████████████████████████████████████████████ 134,313
vertx-pg-client                     ██████████████████████████████████████████████████ 133,788
kora2-jdbc-repository-kotlin        █████████████████████████████████████████████████  132,482
quarkus-jpa-repository              █████████████████████████████████████████████████  132,217
ntex-db-tokio                       █████████████████████████████████████████████████  131,192
quarkus-jpa-ac-reactive             ████████████████████████████████████████████████   130,189
quarkus-jpa-ac                      ████████████████████████████████████████████████   129,913
quarkus-jpa-repository-reactive     ████████████████████████████████████████████████   129,628
micronaut-jdbc-repository           ███████████████████████████████████████████████    126,328
quarkus-jooq                        ███████████████████████████████████████████████    126,234
kora1-jdbc-repository               ██████████████████████████████████████████████     122,413
spring-jdbc-template                █████████████████████████████████████████████      120,594
ktor-netty-jdbc-driver              ██████████████████████████████████████████         112,364
kora1-vertx-repository-suspend      █████████████████████████████████████████          110,486
kora1-jdbc-repository-suspend       ████████████████████████████████████████           108,141
helidon-mp-jdbc-client              ██████████████████████████████████████             102,580
kora1-jdbc-repository-vt            ██████████████████████████████████                  92,195
ktor-cio-jdbc-driver                ██████████████████████████████████                  90,311
micronaut-r2dbc-repository-reactive ████████████████████████                            64,880
spring-r2dbc-repository-reactive    ████████████████████████                            63,799
spring-r2dbc-client-reactive        ████████████████████████                            63,606
spring-jdbc-repository              ███████████████████                                 51,860
spring-jpa-repository               ███████████████████                                 51,616
micronaut-jpa-repository-reactive   █████████████████                                   46,800
helidon-mp-jpa-repository           █████████████████                                   46,571
micronaut-jpa-repository            ███████                                             18,672
```

How to read the table:

- **The top eight are a tie.** They are within 3.6% of each other, a single round has a few percent of
  run-to-run noise, and at this throughput the database host is the limit.
- **Latency maxima of 450–850 ms** appear for most candidates in this run and come with a large standard
  deviation. They are rare stalls on the shared load-generator host, not typical request times, and they
  inflate the averages. Compare tail latency only between candidates of the same run.
- **The ntex result predates its last change.** It was measured with 32 connections per worker (768 in
  total); the module now uses 8 per worker.
- **The lower third is slower for framework reasons**, not because of the environment. It holds Spring Data
  JDBC and JPA, the Micronaut and Helidon JPA candidates, and every R2DBC candidate. Quarkus runs the same
  Hibernate ORM in the leading group, so the cost is in each framework's data layer, not in JPA itself.

## How to run

Prerequisites: Docker, a PostgreSQL instance reachable from the Docker host, and a JDK 25 for the local Kora 2
build.

1. Describe the external database in `.tfb-external-db.env` in the repository root (see
   `.tfb-external-db.env.example`): its IP address and a superuser URL.
2. Prepare the database once:

   ```bash
   ./tfb-ext check
   ./tfb-ext provision
   ./tfb-ext tune --apply
   ```

3. Run the whole suite from the repository root:

   ```bash
   ./run-fair.sh
   ```

   The script builds the Kora 2 distributions and the shared Gradle cache images, then runs
   `./tfb-ext --test <all fair candidates> --type db --concurrency-levels 256 -m benchmark`.

4. Or run selected candidates:

   ```bash
   ./tfb-ext --test fair-kora2-jdbc-repository fair-quarkus-jpa-repository-reactive --type db --concurrency-levels 256 -m benchmark
   ```

Results are written to `results/<timestamp>/results.json`.

Notes:

- `./tfb-ext` behaves like `./tfb`, but skips the throw-away database container and maps the hostname
  `tfb-database` to the external PostgreSQL.
- The Kora 2 candidates are packaged from a locally built distribution (`application.tar`) and resolve Kora
  from the local Maven repository of the machine that runs `run-fair.sh`. Publish the Kora build you want to
  measure there first.
- Containers stopped by TFB can leave idle connections on the external database. Check
  `pg_stat_activity` between long runs if `max_connections` is tight.

Local development of a single module:

```bash
./gradlew :java-kora2-jdbc-repository:test
./gradlew :java-quarkus-jooq:quarkusDev
```

## Known differences between candidates

These are deliberate framework differences or open gaps. They do not affect `/db`, unless stated.

| Difference                                                                                                                                   | Effect                                                                                                                   |
|----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| Connection pool size varies (128–256)                                                                                                        | Affects `/db`: PostgreSQL on this host is most efficient at about 128 active connections; larger pools add database load |
| Kora 2 has tuned thread counts (12 I/O threads + 12 carriers); other candidates use framework defaults                                       | Affects `/db`: about 12% less CPU per request for Kora 2 on this host                                                    |
| `/updates` writes are batched through each framework's own API (`@Batch`, `updateAll`, `batchUpdate`, jOOQ `batch`, R2DBC `Statement.add`, Vert.x `executeBatch`, Hibernate `jdbc.batch_size`); Helidon DbClient has no batch API and sends one `UPDATE` per row | Helidon DbClient does more round trips on `/updates` |
| `/plaintext` and `/json` in the blocking Micronaut candidates run on the Netty event loop; Kora dispatches every request to a virtual thread | Micronaut has no thread handoff on these two endpoints                                                                   |
| ntex can pipeline queries on one connection under high concurrency                                                                           | Possible advantage on `/db` when concurrency exceeds its connection count                                                |
| ntex starts one worker per host CPU even under a container CPU limit                                                                         | Oversubscription in CPU-limited runs                                                                                     |
