package io.techempower.benchmark.kora.model;

import ru.tinkoff.kora.database.common.annotation.Column;
import ru.tinkoff.kora.database.common.annotation.Id;
import ru.tinkoff.kora.database.common.annotation.Table;
import ru.tinkoff.kora.database.jdbc.EntityJdbc;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
@EntityJdbc
public record World(@Id @Column("id") int id,
                    @Column("randomnumber") int randomNumber) {
}
