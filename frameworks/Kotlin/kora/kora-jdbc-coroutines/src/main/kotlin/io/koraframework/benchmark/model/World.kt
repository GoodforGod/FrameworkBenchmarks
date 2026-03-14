package io.koraframework.benchmark.model

import ru.tinkoff.kora.database.common.annotation.Column
import ru.tinkoff.kora.database.common.annotation.Id
import ru.tinkoff.kora.database.jdbc.EntityJdbc
import ru.tinkoff.kora.json.common.annotation.Json

@Json
@EntityJdbc
data class World(
    @field:Id @field:Column("id") val id: Int,
    @field:Column("randomnumber") val randomNumber: Int,
)
