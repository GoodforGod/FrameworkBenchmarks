# Fair Framework Benchmarks на базе TechEmpower FrameworkBenchmarks

[English](README.md) | **Русский**

Этот репозиторий — форк [TechEmpower FrameworkBenchmarks (TFB)](https://github.com/TechEmpower/FrameworkBenchmarks)
с одним дополнением: набором **fair** в [`frameworks/Java/fair`](frameworks/Java/fair). Он сравнивает 26
веб-стеков на JVM и Rust на одинаковых эндпоинтах, с одинаковым SQL, одинаковыми флагами JVM и одним базовым
образом контейнера. Код написан в стиле, который рекомендует документация каждого фреймворка.

**Полное описание кандидатов, сценария и результатов:
[`frameworks/Java/fair/README.ru.md`](frameworks/Java/fair/README.ru.md).**

## Что измеряется

| | |
|---|---|
| Тест | TechEmpower `db`: `GET /db`, один `SELECT` по первичному ключу на запрос |
| Нагрузка | `wrk`, 256 keep-alive соединений, замер 30 с после прогрева 30 с, один раунд на кандидата |
| Кандидаты | 26: Kora 1 и 2, Quarkus, Micronaut, Spring Boot, Helidon, Ktor, Vert.x и Rust ntex |
| База данных | PostgreSQL 18.6 на отдельной машине, подключение через `./tfb-ext` |

## Окружение

| | Хост приложения и генератора нагрузки | Хост базы данных |
|---|---|---|
| CPU | 12 ядер / 24 потока | 8 ядер / 16 потоков |
| Память | 32 ГБ | 32 ГБ |
| ПО | Docker (Linux-контейнеры), JVM-кандидаты на Temurin 25 | PostgreSQL 18.6 в Docker |

Хосты соединены напрямую кабелем Ethernet 2.5 Гбит/с; запрос к БД туда и обратно занимает около 0.15 мс в
покое. Контейнеры работают без лимитов CPU и памяти. `wrk` запускается на хосте приложения.

## Результаты

Прогон от 2026-10-06, тест `db` при 256 соединениях ([`results.json`](results.json)). Все 26 кандидатов;
отклонение и максимум задержек — в [README набора fair](frameworks/Java/fair/README.ru.md#результаты).

**[Интерактивная визуализация результатов на techempower.com](https://www.techempower.com/benchmarks/#section=test&resultsurl=https://raw.githubusercontent.com/GoodforGod/FrameworkBenchmarks/refs/heads/master/results.json&test=db)**

| # | Кандидат | Запросов/с | Задержка, средняя |
|---:|---|---:|---:|
| 1 | Kora 2, JDBC-репозиторий (Java) | 134 313 | 7.20 мс |
| 2 | Клиент Vert.x PostgreSQL | 133 788 | 8.00 мс |
| 3 | Kora 2, JDBC-репозиторий (Kotlin) | 132 482 | 8.14 мс |
| 4 | Quarkus, репозиторий Hibernate ORM Panache | 132 217 | 6.14 мс |
| 5 | Rust ntex + tokio-postgres | 131 192 | 7.33 мс |
| 6 | Quarkus, active record Hibernate Reactive | 130 189 | 6.32 мс |
| 7 | Quarkus, active record Hibernate ORM Panache | 129 913 | 7.53 мс |
| 8 | Quarkus, репозиторий Hibernate Reactive | 129 628 | 6.74 мс |
| 9 | Micronaut Data JDBC | 126 328 | 4.78 мс |
| 10 | Quarkus, jOOQ | 126 234 | 2.03 мс |
| 11 | Kora 1, JDBC-репозиторий | 122 413 | 5.63 мс |
| 12 | Spring Boot, `JdbcTemplate` | 120 594 | 6.10 мс |
| 13 | Ktor Netty, JDBC | 112 364 | 5.21 мс |
| 14 | Kora 1, Vert.x-репозиторий (Kotlin `suspend`) | 110 486 | 5.68 мс |
| 15 | Kora 1, JDBC-репозиторий (Kotlin `suspend`) | 108 141 | 5.29 мс |
| 16 | Helidon MP, DbClient (JDBC) | 102 580 | 11.14 мс |
| 17 | Kora 1, JDBC-репозиторий на виртуальных потоках | 92 195 | 5.53 мс |
| 18 | Ktor CIO, JDBC | 90 311 | 2.72 мс |
| 19 | Micronaut Data R2DBC | 64 880 | 8.97 мс |
| 20 | Spring Boot, Spring Data R2DBC | 63 799 | 10.15 мс |
| 21 | Spring Boot, `DatabaseClient` (R2DBC) | 63 606 | 11.22 мс |
| 22 | Spring Boot, Spring Data JDBC | 51 860 | 11.28 мс |
| 23 | Spring Boot, Spring Data JPA | 51 616 | 10.81 мс |
| 24 | Micronaut Data Hibernate Reactive | 46 800 | 8.77 мс |
| 25 | Helidon MP, репозиторий Helidon Data (JPA) | 46 571 | 13.13 мс |
| 26 | Micronaut Data JPA | 18 672 | 16.05 мс |

Запросов в секунду, тест `db` при 256 соединениях (длиннее — лучше):

```text
kora2-jdbc-repository               ██████████████████████████████████████████████████ 134 313
vertx-pg-client                     ██████████████████████████████████████████████████ 133 788
kora2-jdbc-repository-kotlin        █████████████████████████████████████████████████  132 482
quarkus-jpa-repository              █████████████████████████████████████████████████  132 217
ntex-db-tokio                       █████████████████████████████████████████████████  131 192
quarkus-jpa-ac-reactive             ████████████████████████████████████████████████   130 189
quarkus-jpa-ac                      ████████████████████████████████████████████████   129 913
quarkus-jpa-repository-reactive     ████████████████████████████████████████████████   129 628
micronaut-jdbc-repository           ███████████████████████████████████████████████    126 328
quarkus-jooq                        ███████████████████████████████████████████████    126 234
kora1-jdbc-repository               ██████████████████████████████████████████████     122 413
spring-jdbc-template                █████████████████████████████████████████████      120 594
ktor-netty-jdbc-driver              ██████████████████████████████████████████         112 364
kora1-vertx-repository-suspend      █████████████████████████████████████████          110 486
kora1-jdbc-repository-suspend       ████████████████████████████████████████           108 141
helidon-mp-jdbc-client              ██████████████████████████████████████             102 580
kora1-jdbc-repository-vt            ██████████████████████████████████                  92 195
ktor-cio-jdbc-driver                ██████████████████████████████████                  90 311
micronaut-r2dbc-repository-reactive ████████████████████████                            64 880
spring-r2dbc-repository-reactive    ████████████████████████                            63 799
spring-r2dbc-client-reactive        ████████████████████████                            63 606
spring-jdbc-repository              ███████████████████                                 51 860
spring-jpa-repository               ███████████████████                                 51 616
micronaut-jpa-repository-reactive   █████████████████                                   46 800
helidon-mp-jpa-repository           █████████████████                                   46 571
micronaut-jpa-repository            ███████                                             18 672
```

Первые восемь мест укладываются в 3.6% друг от друга, а это в пределах разброса одного раунда: при такой
нагрузке ограничением служит хост БД. Настоящие различия начинаются ниже по таблице: Spring Data,
JPA-кандидаты Micronaut и Helidon и все кандидаты на R2DBC дают 19–65 тысяч запросов в секунду, тогда как
Quarkus с тем же Hibernate ORM находится в лидирующей группе.

## Как запустить

```bash
# 1. описать внешний PostgreSQL (образец: .tfb-external-db.env.example)
cp .tfb-external-db.env.example .tfb-external-db.env

# 2. один раз подготовить базу данных
./tfb-ext check
./tfb-ext provision
./tfb-ext tune --apply

# 3. запустить всех fair-кандидатов
./run-fair.sh
```

`./tfb-ext` — это `./tfb` с внешней базой данных: он не поднимает временный контейнер `tfb-database`, а
направляет тестовые контейнеры на PostgreSQL из `.tfb-external-db.env`. Запуск части кандидатов:

```bash
./tfb-ext --test fair-kora2-jdbc-repository fair-quarkus-jpa-repository --type db --concurrency-levels 256 -m benchmark
```

Требования и подробности — в [README набора fair](frameworks/Java/fair/README.ru.md#как-запустить).

## Структура репозитория

| Путь | Содержимое |
|---|---|
| [`frameworks/Java/fair`](frameworks/Java/fair) | набор fair: по одному модулю и одному dockerfile на кандидата |
| [`run-fair.sh`](run-fair.sh) | сборка и запуск всего набора fair |
| [`tfb-ext`](tfb-ext), `scripts/external-db` | запуск TFB с внешним PostgreSQL |
| [`results.json`](results.json) | опубликованный прогон |
| `frameworks/`, `toolset/`, `tfb` | фреймворки и инструментарий исходного TechEmpower |

## Дополнительные отчёты

- [Kora 2 против Quarkus reactive, `/db` при c=256](KORA-QUARKUS-DB-C256-REPORT.ru.md): профили CPU, разбор
  веток Kora, подбор пула и потоков.
- [Kora 2 против Quarkus при ограничении CPU контейнера](KORA-QUARKUS-CPU-LIMITED-REPORT.ru.md): 3 и 4 ядра,
  разделение потоков ввода-вывода и carrier-потоков.

## Документация исходного TechEmpower

Всё, что не относится к набору fair, работает как в исходном TFB:

- [Wiki проекта](https://github.com/TechEmpower/FrameworkBenchmarks/wiki): инструментарий, структура файлов, добавление теста
- [Типы тестов и требования](https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview)
- [Официальные результаты](https://www.techempower.com/benchmarks/)
