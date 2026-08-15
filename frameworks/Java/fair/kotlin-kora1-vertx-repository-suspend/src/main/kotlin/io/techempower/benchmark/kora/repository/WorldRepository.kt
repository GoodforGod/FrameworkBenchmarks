package io.techempower.benchmark.kora.repository

import io.techempower.benchmark.kora.model.Fortune
import io.techempower.benchmark.kora.model.World
import ru.tinkoff.kora.database.common.annotation.Query
import ru.tinkoff.kora.database.common.annotation.Repository
import ru.tinkoff.kora.database.vertx.VertxRepository

@Repository
interface WorldRepository : VertxRepository {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    suspend fun findById(id: Int): World

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    suspend fun findRandomNumberById(id: Int): Int

    @Query("UPDATE world SET randomnumber = :randomNumber WHERE id = :id")
    suspend fun updateRandomNumber(id: Int, randomNumber: Int)

    @Query("SELECT id, message FROM fortune")
    suspend fun fortunes(): List<Fortune>
}
