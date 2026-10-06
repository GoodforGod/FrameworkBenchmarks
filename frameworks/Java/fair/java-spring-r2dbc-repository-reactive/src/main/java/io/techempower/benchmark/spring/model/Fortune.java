package io.techempower.benchmark.spring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("fortune")
public record Fortune(@Id int id, @Column("message") String message) {
}
