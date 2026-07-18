package org.springframework.benchmark.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("fortune")
public record Fortune(
    @Id @Column("id") int id,
    @Column("message") String message) {
}
