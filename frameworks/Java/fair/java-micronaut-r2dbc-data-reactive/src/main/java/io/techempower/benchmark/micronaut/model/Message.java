package io.techempower.benchmark.micronaut.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Message(String message) {
}
