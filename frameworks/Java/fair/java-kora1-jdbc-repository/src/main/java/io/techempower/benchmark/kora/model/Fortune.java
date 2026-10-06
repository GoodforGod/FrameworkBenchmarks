package io.techempower.benchmark.kora.model;

import ru.tinkoff.kora.database.common.annotation.Column;
import ru.tinkoff.kora.database.common.annotation.Id;
import ru.tinkoff.kora.database.jdbc.EntityJdbc;

@EntityJdbc
public record Fortune(@Id @Column("id") int id,
                      @Column("message") String message) {
}
