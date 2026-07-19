package io.techempower.benchmark.micronaut.model;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("fortune")
public record Fortune(
        @Id @MappedProperty("id") int id,
        @MappedProperty("message") String message) {
}
