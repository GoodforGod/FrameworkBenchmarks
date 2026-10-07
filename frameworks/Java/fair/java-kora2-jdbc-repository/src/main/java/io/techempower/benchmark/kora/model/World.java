package io.techempower.benchmark.kora.model;

import io.koraframework.database.common.annotation.Column;
import io.koraframework.database.common.annotation.Id;
import io.koraframework.database.jdbc.annotation.EntityJdbc;
import io.koraframework.json.common.annotation.Json;

@Json
@EntityJdbc
public record World(@Id @Column("id") int id,
                    @Column("randomnumber") int randomNumber) {
}
