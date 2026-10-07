package io.koraframework.benchmark.model

import ru.tinkoff.kora.database.common.annotation.Column
import ru.tinkoff.kora.database.common.annotation.Id
import ru.tinkoff.kora.database.jdbc.EntityJdbc

@EntityJdbc
data class Fortune(
    @field:Id @field:Column("id") val id: Int,
    @field:Column("message") val message: String,
)
