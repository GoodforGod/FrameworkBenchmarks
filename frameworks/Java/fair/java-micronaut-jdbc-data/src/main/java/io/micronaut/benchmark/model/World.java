package io.micronaut.benchmark.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record World(int id, int randomNumber) {
}
