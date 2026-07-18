package io.micronaut.benchmark.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Fortune(int id, String message) {
}
