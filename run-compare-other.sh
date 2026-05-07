#!/usr/bin/env bash

./tfb \
  --test \
  kora \
  kora2 \
  micronaut \
  micronaut-loom-on-netty \
  micronaut-jdbc \
  micronaut-r2dbc \
  micronaut-data-jdbc \
  micronaut-data-jdbc-graalvm \
  quarkus \
  quarkus-hibernate-reactive \
  quarkus-reactive-routes-pgclient \
  quarkus-vertx \
  spring \
  spring-data-jdbc \
  spring-webflux \
  undertow-postgresql \
  --type plaintext json db query fortune update \
  --concurrency-levels 16 32 64 \
  --pipeline-concurrency-levels 256 1024 4096 \
  -m benchmark