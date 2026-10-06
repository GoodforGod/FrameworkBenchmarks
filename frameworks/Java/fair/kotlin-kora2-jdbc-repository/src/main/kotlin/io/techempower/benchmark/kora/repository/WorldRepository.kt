package io.techempower.benchmark.kora.repository

import io.techempower.benchmark.kora.model.Fortune
import io.techempower.benchmark.kora.model.World
import io.koraframework.database.common.annotation.Batch
import io.koraframework.database.common.annotation.Query
import io.koraframework.database.common.annotation.Repository
import io.koraframework.database.jdbc.JdbcRepository

@Repository
interface WorldRepository : JdbcRepository {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    fun findById(id: Int): World

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    fun findRandomNumberById(id: Int): Int

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    fun update(@Batch world: List<World>)

    @Query("SELECT id, message FROM fortune")
    fun fortunes(): List<Fortune>
}
