#!/usr/bin/env sh

set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
FAIR_DIR="$SCRIPT_DIR/frameworks/Java/fair"

docker build -t fair-gradle-cache-jdk25:latest -f "$FAIR_DIR/fair-gradle-cache-jdk25.dockerfile" "$FAIR_DIR"
docker build -t fair-gradle-cache-jdk21:latest -f "$FAIR_DIR/fair-gradle-cache-jdk21.dockerfile" "$FAIR_DIR"
docker image inspect fair-gradle-cache-jdk25:latest >/dev/null
docker image inspect fair-gradle-cache-jdk21:latest >/dev/null

cd "$SCRIPT_DIR"

./tfb \
  --test \
  quarkus \
  quarkus-vertx \
  fair-kora2-jdbc \
  fair-kora1-jdbc \
  fair-kora1-jdbc-vt \
  fair-kotlin-kora1-vertx-repository-suspend \
  fair-kotlin-kora1-jdbc-repository-suspend \
  fair-kotlin-kora2-jdbc-repository \
  fair-ktor-netty-jdbc \
  fair-ktor-cio-jdbc \
  fair-helidon-mp-jdbc-client \
  fair-helidon-mp-jpa-repository \
  fair-micronaut-jdbc-repository \
  fair-micronaut-jpa-repository \
  fair-micronaut-jpa-data-reactive \
  fair-micronaut-r2dbc-data-reactive \
  fair-quarkus-jooq \
  fair-quarkus-jpa-ac \
  fair-quarkus-jpa-ac-reactive \
  fair-quarkus-jpa-repository \
  fair-quarkus-jpa-repository-reactive \
  fair-spring-jdbc-repository \
  fair-spring-jdbc-template \
  fair-spring-jpa-repository \
  fair-spring-r2dbc-repository-reactive \
  fair-spring-r2dbc-client-reactive \
  fair-vertx-pg-client \
  ntex-db \
  ntex-db-neon \
  --type db \
  -m verify
