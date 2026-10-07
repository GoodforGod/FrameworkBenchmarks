# Fair Framework Benchmarks on TechEmpower FrameworkBenchmarks

**English** | [Русский](README.ru.md)

This repository is a fork of [TechEmpower FrameworkBenchmarks (TFB)](https://github.com/TechEmpower/FrameworkBenchmarks)
with one addition: the **fair** suite in [`frameworks/Java/fair`](frameworks/Java/fair). It compares 26
JVM and Rust web stacks on the same endpoints, the same SQL, the same JVM flags and the same container image,
using the code style each framework's documentation recommends.

**Full description of the candidates, the scenario and the results:
[`frameworks/Java/fair/README.md`](frameworks/Java/fair/README.md).**

## What is measured

| | |
|---|---|
| Test | TechEmpower `db`: `GET /db`, one `SELECT` by primary key per request |
| Load | `wrk`, 256 keep-alive connections, 30 s measurement after a 30 s warmup, one round per candidate |
| Candidates | 26: Kora 1 and 2, Quarkus, Micronaut, Spring Boot, Helidon, Ktor, Vert.x, and Rust ntex |
| Database | PostgreSQL 18.6 on a separate machine, reached with `./tfb-ext` |

## Environment

| | Application + load generator host | Database host |
|---|---|---|
| CPU | 12 cores / 24 threads | 8 cores / 16 threads |
| Memory | 32 GB | 32 GB |
| Software | Docker (Linux containers), JVM candidates on Temurin 25 | PostgreSQL 18.6 in Docker |

The hosts are connected directly with a 2.5 Gbit/s Ethernet cable; a query round trip takes about 0.15 ms
when idle. Containers run without CPU or memory limits. `wrk` runs on the application host.

## Results

Run of 2026-10-06, `db` test at 256 connections ([`results.json`](results.json)). All 26 candidates;
latency deviation and maximum are in the [fair README](frameworks/Java/fair/README.md#results).

**[Interactive visualisation of the results on techempower.com](https://www.techempower.com/benchmarks/#section=test&resultsurl=https://raw.githubusercontent.com/GoodforGod/FrameworkBenchmarks/refs/heads/master/results.json&test=db)**

| # | Candidate | Requests/s | Latency avg |
|---:|---|---:|---:|
| 1 | Kora 2, JDBC repository (Java) | 134,313 | 7.20 ms |
| 2 | Vert.x PostgreSQL client | 133,788 | 8.00 ms |
| 3 | Kora 2, JDBC repository (Kotlin) | 132,482 | 8.14 ms |
| 4 | Quarkus, Hibernate ORM Panache repository | 132,217 | 6.14 ms |
| 5 | Rust ntex + tokio-postgres | 131,192 | 7.33 ms |
| 6 | Quarkus, Hibernate Reactive active record | 130,189 | 6.32 ms |
| 7 | Quarkus, Hibernate ORM Panache active record | 129,913 | 7.53 ms |
| 8 | Quarkus, Hibernate Reactive repository | 129,628 | 6.74 ms |
| 9 | Micronaut Data JDBC | 126,328 | 4.78 ms |
| 10 | Quarkus, jOOQ | 126,234 | 2.03 ms |
| 11 | Kora 1, JDBC repository | 122,413 | 5.63 ms |
| 12 | Spring Boot, `JdbcTemplate` | 120,594 | 6.10 ms |
| 13 | Ktor Netty, JDBC | 112,364 | 5.21 ms |
| 14 | Kora 1, Vert.x repository (Kotlin `suspend`) | 110,486 | 5.68 ms |
| 15 | Kora 1, JDBC repository (Kotlin `suspend`) | 108,141 | 5.29 ms |
| 16 | Helidon MP, DbClient (JDBC) | 102,580 | 11.14 ms |
| 17 | Kora 1, JDBC repository on virtual threads | 92,195 | 5.53 ms |
| 18 | Ktor CIO, JDBC | 90,311 | 2.72 ms |
| 19 | Micronaut Data R2DBC | 64,880 | 8.97 ms |
| 20 | Spring Boot, Spring Data R2DBC | 63,799 | 10.15 ms |
| 21 | Spring Boot, `DatabaseClient` (R2DBC) | 63,606 | 11.22 ms |
| 22 | Spring Boot, Spring Data JDBC | 51,860 | 11.28 ms |
| 23 | Spring Boot, Spring Data JPA | 51,616 | 10.81 ms |
| 24 | Micronaut Data Hibernate Reactive | 46,800 | 8.77 ms |
| 25 | Helidon MP, Helidon Data repository (JPA) | 46,571 | 13.13 ms |
| 26 | Micronaut Data JPA | 18,672 | 16.05 ms |

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

The first eight are within 3.6% of each other, which is inside the noise of a single round: at this load the
database host is the limit. The real differences start lower in the table: Spring Data, the Micronaut and
Helidon JPA candidates and every R2DBC candidate reach 19–65 thousand requests per second, while Quarkus runs
the same Hibernate ORM in the leading group.

## How to run

```bash
# 1. describe the external PostgreSQL (see .tfb-external-db.env.example)
cp .tfb-external-db.env.example .tfb-external-db.env

# 2. prepare the database once
./tfb-ext check
./tfb-ext provision
./tfb-ext tune --apply

# 3. run all fair candidates
./run-fair.sh
```

`./tfb-ext` is `./tfb` with an external database: it skips the throw-away `tfb-database` container and points
the test containers at the PostgreSQL from `.tfb-external-db.env`. To run a subset:

```bash
./tfb-ext --test fair-kora2-jdbc-repository fair-quarkus-jpa-repository --type db --concurrency-levels 256 -m benchmark
```

Requirements and details are in the [fair README](frameworks/Java/fair/README.md#how-to-run).

## Repository layout

| Path | Contents |
|---|---|
| [`frameworks/Java/fair`](frameworks/Java/fair) | the fair suite: one module and one dockerfile per candidate |
| [`run-fair.sh`](run-fair.sh) | builds and runs the whole fair suite |
| [`tfb-ext`](tfb-ext), `scripts/external-db` | running TFB against an external PostgreSQL |
| [`results.json`](results.json) | the published run |
| `frameworks/`, `toolset/`, `tfb` | upstream TechEmpower frameworks and toolset |

## Upstream TechEmpower documentation

Everything that is not specific to the fair suite works as in upstream TFB:

- [Project wiki](https://github.com/TechEmpower/FrameworkBenchmarks/wiki): toolset, file structure, adding a test
- [Test types and requirements](https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview)
- [Official results](https://www.techempower.com/benchmarks/)
