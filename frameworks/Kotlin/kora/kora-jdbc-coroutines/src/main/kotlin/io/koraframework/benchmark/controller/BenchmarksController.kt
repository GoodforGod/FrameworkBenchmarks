package io.koraframework.benchmark.controller

import io.koraframework.benchmark.model.Fortune
import io.koraframework.benchmark.model.Message
import io.koraframework.benchmark.model.World
import io.koraframework.benchmark.repository.WorldRepository
import io.koraframework.benchmark.util.QueryUtils
import jakarta.annotation.Nullable
import ru.tinkoff.kora.common.Component
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

    @HttpRoute(method = HttpMethod.GET, path = "/plaintext")
    suspend fun plaintext(): HttpServerResponse {
        return HttpServerResponse.of(200, HttpBody.plaintext(PLAINTEXT_RESPONSE.duplicate()))
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

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/queries")
    suspend fun queries(@Nullable @Query("queries") queries: Int?): List<World> {
        val count = QueryUtils.parseCount(queries)
        val ids = ArrayList<Int>(count)
        repeat(count) {
            ids += QueryUtils.randomWorld()
        }
        return repository.findById(ids)
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    suspend fun updates(@Nullable @Query("queries") queries: Int?): List<World> {
        val count = QueryUtils.parseCount(queries)
        val ids = ArrayList<Int>(count)
        repeat(count) {
            ids += QueryUtils.randomWorld()
        }

        val worlds = repository.findById(ids).mapTo(ArrayList(count)) { world ->
            World(world.id, QueryUtils.randomWorld())
        }
        worlds.sortBy(World::id)
        repository.update(worlds)
        return worlds
    }

    @HttpRoute(method = HttpMethod.GET, path = "/fortunes")
    suspend fun fortunes(): List<Fortune> {
        val fortunes = repository.fortunes().toMutableList()
        fortunes += Fortune(0, "Additional fortune added at request time.")
        fortunes.sortBy(Fortune::message)
        return fortunes
    }

    private companion object {
        val PLAINTEXT_RESPONSE: ByteBuffer = ByteBuffer.wrap("Hello, World!".toByteArray(StandardCharsets.UTF_8))
        val MESSAGE = Message("Hello, World!")
    }
}
