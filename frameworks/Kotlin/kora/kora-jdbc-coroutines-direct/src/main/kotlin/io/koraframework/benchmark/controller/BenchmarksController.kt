package io.koraframework.benchmark.controller

import io.koraframework.benchmark.model.Fortune
import io.koraframework.benchmark.model.Message
import io.koraframework.benchmark.model.World
import io.koraframework.benchmark.repository.WorldRepository
import io.koraframework.benchmark.util.QueryUtils
import jakarta.annotation.Nullable
import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.database.jdbc.JdbcHelper.SqlFunction0
import ru.tinkoff.kora.database.jdbc.withConnectionCtxSuspend
import ru.tinkoff.kora.http.common.HttpMethod
import ru.tinkoff.kora.http.common.annotation.HttpRoute
import ru.tinkoff.kora.http.common.annotation.Query
import ru.tinkoff.kora.http.common.body.HttpBody
import ru.tinkoff.kora.http.server.common.HttpServerResponse
import ru.tinkoff.kora.http.server.common.annotation.HttpController
import ru.tinkoff.kora.json.common.annotation.Json
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@Component
@HttpController
class BenchmarksController(
    private val repository: WorldRepository,
) {

    private companion object {
        val PLAINTEXT_RESPONSE: ByteBuffer = ByteBuffer.wrap("Hello, World!".toByteArray(StandardCharsets.UTF_8))
        val MESSAGE = Message("Hello, World!")
        val FORTUNE_COMPARATOR: Comparator<Fortune> = Comparator.comparing(Fortune::message)
        val WORLD_COMPARATOR: Comparator<World> = Comparator.comparingInt(World::id)
    }

    @HttpRoute(method = HttpMethod.GET, path = "/plaintext")
    suspend fun plaintext(): HttpServerResponse {
        return HttpServerResponse.of(200, HttpBody.plaintext(PLAINTEXT_RESPONSE))
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/json")
    suspend fun json(): Message {
        return MESSAGE
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/db")
    suspend fun db(): World {
        return repository.findById(QueryUtils.randomWorld())
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/queries")
    suspend fun queries(@Nullable @Query("queries") queries: String?): List<World> {
        val count = QueryUtils.parseCount(queries)
        val worlds: MutableList<World> = ArrayList(count)
        for (i in 0 until count) {
            val id = QueryUtils.randomWorld()
            val world = repository.findById(id)
            worlds.add(world)
        }

        return worlds
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    suspend fun updates(@Nullable @Query("queries") queries: String?): List<World> {
        val count = QueryUtils.parseCount(queries)
        val worlds: MutableList<World> = ArrayList(count)

        for (i in 0 until count) {
            val id = QueryUtils.randomWorld()
            val oldWorld = repository.findById(id)
            val newWorld = World(oldWorld.id, QueryUtils.randomWorld())
            worlds.add(newWorld)
        }

        repository.update(worlds)
        worlds.sortWith(WORLD_COMPARATOR)
        return worlds
    }

    @HttpRoute(method = HttpMethod.GET, path = "/fortunes")
    suspend fun fortunes(): List<Fortune> {
        val fortunes = repository.fortunes().toMutableList()
        fortunes += Fortune(0, "Additional fortune added at request time.")

        fortunes.sortWith(FORTUNE_COMPARATOR)
        return fortunes
    }
}
