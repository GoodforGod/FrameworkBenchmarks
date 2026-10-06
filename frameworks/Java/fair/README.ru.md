# Fair Framework Benchmarks

[English](README.md) | **Русский**

Набор из 26 кандидатов в стиле TechEmpower, собранный для сравнения **в равных условиях**: одинаковые
эндпоинты, одинаковый SQL, одинаковые флаги JVM и базовый образ контейнера, а код написан так, как
рекомендует документация каждого фреймворка, без приёмов «только для бенчмарка». В наборе Kora 1 и 2, Quarkus,
Micronaut, Spring Boot, Helidon, Ktor и Vert.x на JVM, плюс один кандидат на Rust (ntex) как ориентир вне JVM.

- [Что здесь значит «fair»](#что-здесь-значит-fair)
- [Сценарий](#сценарий)
- [Окружение](#окружение)
- [Кандидаты](#кандидаты)
- [Результаты](#результаты)
- [Как запустить](#как-запустить)
- [Известные различия между кандидатами](#известные-различия-между-кандидатами)

## Что здесь значит «fair»

| Правило                        | Как оно применяется                                                                                                                                        |
|--------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Одинаковая работа на запрос    | `/db` выполняет ровно один `SELECT id, randomnumber FROM world WHERE id = ?` на запрос. Нет кеширования результатов и объединения запросов разных клиентов |
| Идиоматичный код               | Каждый кандидат использует документированный способ написать контроллер и репозиторий: декларативные репозитории, JPA, R2DBC, `JdbcTemplate` и так далее   |
| Одинаковая среда выполнения    | Все JVM-кандидаты работают на `eclipse-temurin:25-jre-jammy` с одинаковыми `JAVA_OPTS`                                                                     |
| Одинаковые настройки драйвера  | PostgreSQL JDBC `42.7.13` с серверными prepared statements (`prepareThreshold=1`) у всех JDBC-кандидатов                                                   |
| Блокирующий код вне event loop | Блокирующие эндпоинты JDBC/JPA выполняются на виртуальных потоках или блокирующем диспетчере, но не на потоке ввода-вывода                                 |
| Без приёмов с pipelining       | Каждый запрос ждёт собственного обмена с БД                                                                                                                |

Проверено профилированием для Kora 2, Quarkus reactive и Micronaut JDBC: один оператор на запрос на стороне
Postgres, один обмен Bind/Execute/Sync, два сетевых пакета на запрос к БД.

## Сценарий

| Параметр           | Значение                                                                                         |
|--------------------|--------------------------------------------------------------------------------------------------|
| Тест               | TechEmpower `db` (один запрос к БД), `GET /db`                                                   |
| Генератор нагрузки | `wrk` из набора TFB, 24 потока, keep-alive                                                       |
| Конкурентность     | 256 соединений                                                                                   |
| Фазы на кандидата  | разогрев (5 с, 8 соединений) → прогрев (30 с, 256 соединений) → замер (**30 с**, 256 соединений) |
| Раунды             | 1 на кандидата; кандидаты запускаются по очереди, каждый в новом контейнере                      |
| База данных        | внешний PostgreSQL на отдельной машине (`./tfb-ext`), таблица `world` на 10 000 строк            |
| Метрика            | запросов в секунду = всего запросов ÷ 30 с; задержки по данным wrk                               |

Остальные эндпоинты TFB (`/plaintext`, `/json`, `/queries`, `/updates`, `/fortunes`) реализованы у каждого
кандидата, но в опубликованном прогоне измерялся только `/db`.

## Окружение

|                     | Хост приложения и генератора нагрузки                                            | Хост базы данных         |
|---------------------|----------------------------------------------------------------------------------|--------------------------|
| CPU                 | 12 ядер / 24 потока                                                              | 8 ядер / 16 потоков      |
| Память              | 32 ГБ                                                                            | 32 ГБ                    |
| ПО                  | Docker (Linux-контейнеры); контейнер приложения и контейнер `wrk` на одном хосте | PostgreSQL 18.6 в Docker |
| Лимиты CPU и памяти | нет: контейнер приложения видит все 24 потока                                    | нет                      |

- **Сеть:** хосты соединены напрямую кабелем Ethernet 2.5 Гбит/с. Один запрос к БД туда и обратно занимает
  около 0.15 мс в покое и около 1.5 мс при 256 соединениях.
- **Настройки PostgreSQL:** `max_connections=2000`, `synchronous_commit=off`, `shared_buffers=256MB`,
  включён `pg_stat_statements`.
- **Флаги JVM (все JVM-кандидаты):** `-XX:+UseParallelGC -XX:+UseNUMA -XX:+UseCompactObjectHeaders
  -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=64m -XX:MaxInlineLevel=20
  -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false`. Размер кучи не задан, действует значение JVM
  по умолчанию (четверть памяти хоста).
- **Общий хост:** `wrk` и приложение делят одни и те же 24 потока CPU.

При такой нагрузке хост БД близок к насыщению для самых быстрых кандидатов, поэтому верх таблицы ограничен
PostgreSQL, а не фреймворками.

## Кандидаты

Версии: Kora `1.2.22` / `2.0.0.RC2`, Quarkus `3.40.1`, Micronaut `5.2.1`, Spring Boot `4.1.1`,
Helidon `4.5.5`, Ktor `3.6.0`, Vert.x `5.2.0`, Hibernate ORM `7.4.5`, jOOQ `3.21.9`, HikariCP `7.1.0`,
Kotlin `2.4.20`, ntex `3.12.3`.

«Где выполняется `/db`» — поток, на котором работают метод контроллера и запрос к БД.

### Kora

| Тест TFB                              | Модуль                                                                         | HTTP-сервер | Доступ к данным                          | Где выполняется `/db`                                                                   | Макс. соединений с БД |
|---------------------------------------|--------------------------------------------------------------------------------|-------------|------------------------------------------|-----------------------------------------------------------------------------------------|----------------------:|
| `fair-kora2-jdbc-repository`          | [java-kora2-jdbc-repository](java-kora2-jdbc-repository)                       | Undertow    | JDBC-репозиторий Kora                    | один виртуальный поток на HTTP-соединение; ответ отправляет поток ввода-вывода Undertow |                   128 |
| `fair-kora2-jdbc-repository-kotlin`   | [kotlin-kora2-jdbc-repository](kotlin-kora2-jdbc-repository)                   | Undertow    | JDBC-репозиторий Kora (Kotlin)           | так же                                                                                  |                   128 |
| `fair-kora1-jdbc-repository`          | [java-kora1-jdbc-repository](java-kora1-jdbc-repository)                       | Undertow    | JDBC-репозиторий Kora 1                  | пул платформенных рабочих потоков                                                       |                   256 |
| `fair-kora1-jdbc-repository-vt`       | [java-kora1-jdbc-repository](java-kora1-jdbc-repository)                       | Undertow    | JDBC-репозиторий Kora 1                  | виртуальные потоки (`VIRTUAL_THREADS_ENABLED=true`)                                     |                   256 |
| `fair-kora1-jdbc-repository-suspend`  | [kotlin-kora1-jdbc-repository-suspend](kotlin-kora1-jdbc-repository-suspend)   | Undertow    | JDBC-репозиторий Kora 1, `suspend` API   | корутина; JDBC на `Dispatchers.IO`                                                      |                   256 |
| `fair-kora1-vertx-repository-suspend` | [kotlin-kora1-vertx-repository-suspend](kotlin-kora1-vertx-repository-suspend) | Undertow    | Vert.x-репозиторий Kora 1, `suspend` API | корутина; неблокирующий клиент Vert.x PostgreSQL                                        |                   256 |

У кандидатов Kora 2 заданы `httpServer.undertow.ioThreads = 12` и
`-Djdk.virtualThreadScheduler.parallelism=12`: половина из 24 потоков CPU под ввод-вывод Undertow, половина
под carrier-потоки виртуальных потоков.

### Quarkus

| Тест TFB                               | Модуль                                                                       | HTTP-сервер           | Доступ к данным                           | Где выполняется `/db`                               | Макс. соединений с БД |
|----------------------------------------|------------------------------------------------------------------------------|-----------------------|-------------------------------------------|-----------------------------------------------------|----------------------:|
| `fair-quarkus-jooq`                    | [java-quarkus-jooq](java-quarkus-jooq)                                       | Vert.x (Quarkus REST) | jOOQ DSL поверх JDBC                      | виртуальный поток на запрос (`@RunOnVirtualThread`) |                   256 |
| `fair-quarkus-jpa-ac`                  | [java-quarkus-jpa-ac](java-quarkus-jpa-ac)                                   | Vert.x (Quarkus REST) | Hibernate ORM Panache, active record      | виртуальный поток на запрос                         |                   256 |
| `fair-quarkus-jpa-repository`          | [java-quarkus-jpa-repository](java-quarkus-jpa-repository)                   | Vert.x (Quarkus REST) | Hibernate ORM Panache, репозиторий        | виртуальный поток на запрос                         |                   256 |
| `fair-quarkus-jpa-ac-reactive`         | [java-quarkus-jpa-ac-reactive](java-quarkus-jpa-ac-reactive)                 | Vert.x (Quarkus REST) | Hibernate Reactive Panache, active record | event loop Vert.x                                   |                   256 |
| `fair-quarkus-jpa-repository-reactive` | [java-quarkus-jpa-repository-reactive](java-quarkus-jpa-repository-reactive) | Vert.x (Quarkus REST) | Hibernate Reactive Panache, репозиторий   | event loop Vert.x                                   |                   256 |

### Micronaut

| Тест TFB                                   | Модуль                                                                               | HTTP-сервер | Доступ к данным                    | Где выполняется `/db`                                | Макс. соединений с БД |
|--------------------------------------------|--------------------------------------------------------------------------------------|-------------|------------------------------------|------------------------------------------------------|----------------------:|
| `fair-micronaut-jdbc-repository`           | [java-micronaut-jdbc-repository](java-micronaut-jdbc-repository)                     | Netty       | Micronaut Data JDBC                | виртуальный поток на запрос (`@ExecuteOn(BLOCKING)`) |                   256 |
| `fair-micronaut-jpa-repository`            | [java-micronaut-jpa-repository](java-micronaut-jpa-repository)                       | Netty       | Micronaut Data JPA (Hibernate ORM) | виртуальный поток на запрос (`@ExecuteOn(BLOCKING)`) |                   256 |
| `fair-micronaut-jpa-repository-reactive`   | [java-micronaut-jpa-repository-reactive](java-micronaut-jpa-repository-reactive)     | Netty       | Micronaut Data Hibernate Reactive  | event loop Netty                                     |                   256 |
| `fair-micronaut-r2dbc-repository-reactive` | [java-micronaut-r2dbc-repository-reactive](java-micronaut-r2dbc-repository-reactive) | Netty       | Micronaut Data R2DBC               | event loop Netty                                     |                   256 |

### Spring Boot

| Тест TFB                                | Модуль                                                                         | HTTP-сервер   | Доступ к данным                 | Где выполняется `/db`       | Макс. соединений с БД |
|-----------------------------------------|--------------------------------------------------------------------------------|---------------|---------------------------------|-----------------------------|----------------------:|
| `fair-spring-jdbc-template`             | [java-spring-jdbc-template](java-spring-jdbc-template)                         | Tomcat        | `JdbcTemplate`                  | виртуальный поток на запрос |                   256 |
| `fair-spring-jdbc-repository`           | [java-spring-jdbc-repository](java-spring-jdbc-repository)                     | Tomcat        | Spring Data JDBC                | виртуальный поток на запрос |                   256 |
| `fair-spring-jpa-repository`            | [java-spring-jpa-repository](java-spring-jpa-repository)                       | Tomcat        | Spring Data JPA (Hibernate ORM) | виртуальный поток на запрос |                   256 |
| `fair-spring-r2dbc-client-reactive`     | [java-spring-r2dbc-client-reactive](java-spring-r2dbc-client-reactive)         | Reactor Netty | `DatabaseClient` (R2DBC)        | event loop Netty            |                   256 |
| `fair-spring-r2dbc-repository-reactive` | [java-spring-r2dbc-repository-reactive](java-spring-r2dbc-repository-reactive) | Reactor Netty | Spring Data R2DBC               | event loop Netty            |                   256 |

### Helidon, Ktor, Vert.x, Rust

| Тест TFB                         | Модуль                                                           | HTTP-сервер       | Доступ к данным                              | Где выполняется `/db`                                   | Макс. соединений с БД |
|----------------------------------|------------------------------------------------------------------|-------------------|----------------------------------------------|---------------------------------------------------------|----------------------:|
| `fair-helidon-mp-jdbc-client`    | [java-helidon-mp-jdbc-client](java-helidon-mp-jdbc-client)       | Helidon WebServer | Helidon DbClient (JDBC)                      | виртуальный поток на запрос                             |                   256 |
| `fair-helidon-mp-jpa-repository` | [java-helidon-mp-jpa-repository](java-helidon-mp-jpa-repository) | Helidon WebServer | репозиторий Helidon Data (JPA)               | виртуальный поток на запрос                             |                   256 |
| `fair-ktor-netty-jdbc-driver`    | [kotlin-ktor-netty-jdbc-driver](kotlin-ktor-netty-jdbc-driver)   | Netty             | HikariCP + обычный JDBC                      | корутина; JDBC на `Dispatchers.IO`                      |                   256 |
| `fair-ktor-cio-jdbc-driver`      | [kotlin-ktor-cio-jdbc-driver](kotlin-ktor-cio-jdbc-driver)       | Ktor CIO          | HikariCP + обычный JDBC                      | корутина; JDBC на `Dispatchers.IO`                      |                   256 |
| `fair-vertx-pg-client`           | [java-vertx-pg-client](java-vertx-pg-client)                     | Vert.x Web        | клиент Vert.x PostgreSQL, лимит pipelining 1 | event loop Vert.x, один verticle на поток CPU           |                   256 |
| `fair-ntex-db-tokio`             | [rust-ntex-db-tokio](rust-ntex-db-tokio)                         | ntex              | `tokio-postgres`                             | асинхронная задача на рабочем потоке (по одному на CPU) |      8 на поток (192) |

У каждого модуля есть свой README с командами сборки и конфигурацией.

## Результаты

Прогон от 2026-10-06: тест `db`, 256 соединений, замер 30 с, один раунд. Ни один кандидат не вернул ошибок.
Источник: [`results.json`](../../../results.json) в корне репозитория.

|  # | Кандидат                                   | Запросов/с | Задержка, средняя | Задержка, ст. откл. | Задержка, макс. |
|---:|--------------------------------------------|-----------:|------------------:|--------------------:|----------------:|
|  1 | `fair-kora2-jdbc-repository`               |    134 313 |           7.20 мс |            46.02 мс |       609.87 мс |
|  2 | `fair-vertx-pg-client`                     |    133 788 |           8.00 мс |            50.85 мс |       660.33 мс |
|  3 | `fair-kora2-jdbc-repository-kotlin`        |    132 482 |           8.14 мс |            52.22 мс |       674.86 мс |
|  4 | `fair-quarkus-jpa-repository`              |    132 217 |           6.14 мс |            38.61 мс |       550.97 мс |
|  5 | `fair-ntex-db-tokio`                       |    131 192 |           7.33 мс |            46.80 мс |       648.97 мс |
|  6 | `fair-quarkus-jpa-ac-reactive`             |    130 189 |           6.32 мс |            38.38 мс |       549.80 мс |
|  7 | `fair-quarkus-jpa-ac`                      |    129 913 |           7.53 мс |            47.66 мс |       636.20 мс |
|  8 | `fair-quarkus-jpa-repository-reactive`     |    129 628 |           6.74 мс |            41.69 мс |       586.20 мс |
|  9 | `fair-micronaut-jdbc-repository`           |    126 328 |           4.78 мс |            28.29 мс |       444.94 мс |
| 10 | `fair-quarkus-jooq`                        |    126 234 |           2.03 мс |             1.53 мс |        66.41 мс |
| 11 | `fair-kora1-jdbc-repository`               |    122 413 |           5.63 мс |            32.25 мс |       515.70 мс |
| 12 | `fair-spring-jdbc-template`                |    120 594 |           6.10 мс |            37.73 мс |       537.06 мс |
| 13 | `fair-ktor-netty-jdbc-driver`              |    112 364 |           5.21 мс |            29.68 мс |       459.73 мс |
| 14 | `fair-kora1-vertx-repository-suspend`      |    110 486 |           5.68 мс |            32.41 мс |       504.91 мс |
| 15 | `fair-kora1-jdbc-repository-suspend`       |    108 141 |           5.29 мс |            30.04 мс |       457.11 мс |
| 16 | `fair-helidon-mp-jdbc-client`              |    102 580 |          11.14 мс |            66.90 мс |       840.04 мс |
| 17 | `fair-kora1-jdbc-repository-vt`            |     92 195 |           5.53 мс |            28.22 мс |       456.80 мс |
| 18 | `fair-ktor-cio-jdbc-driver`                |     90 311 |           2.72 мс |             1.13 мс |        29.10 мс |
| 19 | `fair-micronaut-r2dbc-repository-reactive` |     64 880 |           8.97 мс |            43.43 мс |       618.98 мс |
| 20 | `fair-spring-r2dbc-repository-reactive`    |     63 799 |          10.15 мс |            50.79 мс |       677.52 мс |
| 21 | `fair-spring-r2dbc-client-reactive`        |     63 606 |          11.22 мс |            57.62 мс |       850.33 мс |
| 22 | `fair-spring-jdbc-repository`              |     51 860 |          11.28 мс |            52.56 мс |       661.68 мс |
| 23 | `fair-spring-jpa-repository`               |     51 616 |          10.81 мс |            50.13 мс |       641.49 мс |
| 24 | `fair-micronaut-jpa-repository-reactive`   |     46 800 |           8.77 мс |            32.12 мс |       481.46 мс |
| 25 | `fair-helidon-mp-jpa-repository`           |     46 571 |          13.13 мс |            61.65 мс |       765.62 мс |
| 26 | `fair-micronaut-jpa-repository`            |     18 672 |          16.05 мс |            30.38 мс |       483.73 мс |

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

Как читать таблицу:

- **Первые восемь мест — фактически ничья.** Они укладываются в 3.6% друг от друга, у одного раунда разброс в
  несколько процентов, а на такой пропускной способности ограничением служит хост БД.
- **Максимальные задержки 450–850 мс** в этом прогоне есть у большинства кандидатов и сопровождаются большим
  стандартным отклонением. Это редкие паузы на общем хосте с генератором нагрузки, а не типичное время
  запроса, и они завышают средние значения. Хвосты задержек сравнивайте только внутри одного прогона.
- **Результат ntex получен до последнего изменения модуля.** Он измерен с 32 соединениями на рабочий поток
  (768 всего); сейчас модуль использует 8 на поток.
- **Нижняя треть медленнее из-за самих фреймворков**, а не окружения. В ней Spring Data JDBC и JPA,
  JPA-кандидаты Micronaut и Helidon и все кандидаты на R2DBC. Quarkus с тем же Hibernate ORM находится в
  лидирующей группе, так что дело в слое доступа к данным каждого фреймворка, а не в JPA как таковом.

## Как запустить

Нужны Docker, PostgreSQL, доступный с хоста Docker, и JDK 25 для локальной сборки Kora 2.

1. Опишите внешнюю БД в файле `.tfb-external-db.env` в корне репозитория (образец —
   `.tfb-external-db.env.example`): IP-адрес и URL суперпользователя.
2. Один раз подготовьте БД:

   ```bash
   ./tfb-ext check
   ./tfb-ext provision
   ./tfb-ext tune --apply
   ```

3. Запустите весь набор из корня репозитория:

   ```bash
   ./run-fair.sh
   ```

   Скрипт собирает дистрибутивы Kora 2 и общие образы с кешем Gradle, затем выполняет
   `./tfb-ext --test <все fair-кандидаты> --type db --concurrency-levels 256 -m benchmark`.

4. Или запустите отдельных кандидатов:

   ```bash
   ./tfb-ext --test fair-kora2-jdbc-repository fair-quarkus-jpa-repository-reactive --type db --concurrency-levels 256 -m benchmark
   ```

Результаты записываются в `results/<метка времени>/results.json`.

Примечания:

- `./tfb-ext` работает как `./tfb`, но не поднимает временный контейнер с БД, а сопоставляет имя хоста
  `tfb-database` с внешним PostgreSQL.
- Кандидаты Kora 2 упаковываются из локально собранного дистрибутива (`application.tar`) и берут Kora из
  локального репозитория Maven той машины, на которой запускается `run-fair.sh`. Сначала опубликуйте туда
  сборку Kora, которую хотите измерить.
- Контейнеры, остановленные TFB, могут оставлять простаивающие соединения на внешней БД. Между длинными
  прогонами проверяйте `pg_stat_activity`, если запас по `max_connections` небольшой.

Локальная разработка отдельного модуля:

```bash
./gradlew :java-kora2-jdbc-repository:test
./gradlew :java-quarkus-jooq:quarkusDev
```

## Известные различия между кандидатами

Это осознанные различия фреймворков или открытые расхождения. На `/db` они не влияют, если не указано иное.

| Различие                                                                                                                                    | Эффект                                                                                                                                  |
|---------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Размер пула соединений разный (128–256)                                                                                                     | Влияет на `/db`: PostgreSQL на этом хосте эффективнее всего примерно при 128 активных соединениях; пулы больше добавляют нагрузку на БД |
| У Kora 2 настроено число потоков (12 потоков ввода-вывода + 12 carrier); остальные используют значения по умолчанию                         | Влияет на `/db`: примерно на 12% меньше CPU на запрос у Kora 2 на этом хосте                                                            |
| `/updates`: Kora отправляет один JDBC-batch; Micronaut JDBC и ntex отправляют по одному `UPDATE` на строку                                  | У Kora меньше обменов с БД на `/updates`                                                                                                |
| `/plaintext` и `/json` у блокирующих кандидатов Micronaut выполняются на event loop Netty; Kora передаёт каждый запрос на виртуальный поток | У Micronaut нет передачи между потоками на этих двух эндпоинтах                                                                         |
| ntex может отправлять несколько запросов по одному соединению подряд (pipelining) при высокой конкурентности                                | Возможное преимущество на `/db`, когда конкурентность превышает число его соединений                                                    |
| ntex запускает по одному рабочему потоку на CPU хоста даже при лимите CPU контейнера                                                        | Избыток потоков в прогонах с ограничением CPU                                                                                           |

Подробные замеры, профили CPU и разбор Kora 2 против Quarkus:
[KORA-QUARKUS-DB-C256-REPORT.ru.md](../../../KORA-QUARKUS-DB-C256-REPORT.ru.md) и
[KORA-QUARKUS-CPU-LIMITED-REPORT.ru.md](../../../KORA-QUARKUS-CPU-LIMITED-REPORT.ru.md).
