package io.techempower.benchmark.kora.model;

import io.koraframework.database.common.annotation.Column;
import io.koraframework.database.common.annotation.Id;
import io.koraframework.database.jdbc.annotation.EntityJdbc;

@EntityJdbc
public record Fortune(@Id @Column("id") int id,
                      @Column("message") String message) {
}
