package io.techempower.benchmark.spring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Table("world")
public record World(
    @Id @Column("id") @JsonProperty("id") int id,
    @Column("randomnumber") @JsonProperty("randomNumber") int randomNumber) {
}
