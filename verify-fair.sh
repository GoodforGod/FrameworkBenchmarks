#!/usr/bin/env sh

set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
FAIR_DIR="$SCRIPT_DIR/frameworks/Java/fair"
JAVA_KORA2_DIR="$FAIR_DIR/java-kora2-jdbc-repository"
KOTLIN_KORA2_DIR="$FAIR_DIR/kotlin-kora2-jdbc-repository"

echo "Building local Kora 2 snapshot distributions"
"$FAIR_DIR/gradlew" -p "$FAIR_DIR" --refresh-dependencies --rerun-tasks \
  :java-kora2-jdbc-repository:distTar \
  :kotlin-kora2-jdbc-repository:distTar

stage_application_tar() {
  project_dir="$1"
  source_tar="$project_dir/build/distributions/application.tar"
  target_tar="$project_dir/application.tar"

  test -s "$source_tar"
  cp "$source_tar" "$target_tar"
  test -s "$target_tar"
  echo "Staged $target_tar"
}

stage_application_tar "$JAVA_KORA2_DIR"
stage_application_tar "$KOTLIN_KORA2_DIR"

docker build -t fair-gradle-cache-jdk25:latest -f "$FAIR_DIR/fair-gradle-cache-jdk25.dockerfile" "$FAIR_DIR"
docker build -t fair-gradle-cache-jdk21:latest -f "$FAIR_DIR/fair-gradle-cache-jdk21.dockerfile" "$FAIR_DIR"
docker image inspect fair-gradle-cache-jdk25:latest >/dev/null
docker image inspect fair-gradle-cache-jdk21:latest >/dev/null

cd "$SCRIPT_DIR"

./tfb \
  --test \
  fair-kora1-jdbc-repository \
  fair-kora1-jdbc-repository-vt \
  fair-kora1-vertx-repository-suspend \
  fair-kora1-jdbc-repository-suspend \
  fair-kora2-jdbc-repository \
  fair-kora2-jdbc-repository-kotlin \
  fair-ktor-netty-jdbc-driver \
  fair-ktor-cio-jdbc-driver \
  fair-helidon-mp-jdbc-client \
  fair-helidon-mp-jpa-repository \
  fair-micronaut-jdbc-repository \
  fair-micronaut-jpa-repository \
  fair-micronaut-jpa-repository-reactive \
  fair-micronaut-r2dbc-repository-reactive \
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
  fair-ntex-db-tokio \
  --type db \
  -m verify
