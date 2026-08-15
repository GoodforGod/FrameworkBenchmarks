package io.techempower.benchmark.kora.model

import io.koraframework.database.common.annotation.Column
import io.koraframework.database.common.annotation.Id
import io.koraframework.database.jdbc.annotation.EntityJdbc
import io.koraframework.json.common.annotation.Json

@Json
@EntityJdbc
data class World(
    @field:Id @field:Column("id") val id: Int,
    @field:Column("randomnumber") val randomNumber: Int,
)
