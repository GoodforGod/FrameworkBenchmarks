package io.koraframework.benchmark.repository

import io.koraframework.benchmark.model.Fortune
import io.koraframework.benchmark.model.World
import ru.tinkoff.kora.database.common.annotation.Batch
import ru.tinkoff.kora.database.common.annotation.Query
import ru.tinkoff.kora.database.common.annotation.Repository
import ru.tinkoff.kora.database.jdbc.JdbcRepository

@Repository
interface WorldRepository : JdbcRepository {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    suspend fun findById(id: Int): World

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    suspend fun update(@Batch world: List<World>)

    @Query("SELECT id, message FROM fortune")
    suspend fun fortunes(): List<Fortune>
}
