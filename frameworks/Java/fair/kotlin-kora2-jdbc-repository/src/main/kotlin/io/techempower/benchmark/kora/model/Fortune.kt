package io.techempower.benchmark.kora.model

import io.koraframework.database.common.annotation.Column
import io.koraframework.database.common.annotation.Id
import io.koraframework.database.jdbc.annotation.EntityJdbc

@EntityJdbc
data class Fortune(
    @field:Id @field:Column("id") val id: Int,
    @field:Column("message") val message: String,
)
