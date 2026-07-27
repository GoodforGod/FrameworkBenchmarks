package io.techempower.benchmark.micronaut.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
@MappedEntity("world")
public record World(
        @Id @MappedProperty("id") int id,
        @MappedProperty("randomnumber") int randomNumber) {
}
