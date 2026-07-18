package io.spring.benchmark.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("world")
public record World(@Id int id, @Column("randomnumber") int randomNumber) {
}
