package io.techempower.benchmark.kora.model

import ru.tinkoff.kora.database.common.annotation.Column
import ru.tinkoff.kora.database.common.annotation.Id

data class Fortune(
    @field:Id @field:Column("id") val id: Int,
    @field:Column("message") val message: String,
)
