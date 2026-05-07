#!/usr/bin/env bash

./tfb \
  --test \
  kora \
  kora-vt \
  korad \
  quarkus \
  spring-data-jdbc \
  micronaut-data-jdbc \
  --type query \
  --concurrency-levels 32 \
  --pipeline-concurrency-levels 256 1024 \
  -m benchmark