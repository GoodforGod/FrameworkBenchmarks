package io.techempower.benchmark.kora.controller

import io.techempower.benchmark.kora.model.Fortune
import io.techempower.benchmark.kora.model.Message
import io.techempower.benchmark.kora.model.World
import io.techempower.benchmark.kora.repository.WorldRepository
import io.techempower.benchmark.kora.util.QueryUtils
import io.koraframework.common.annotation.Component
import io.koraframework.http.common.HttpMethod
import io.koraframework.http.common.annotation.HttpRoute
import io.koraframework.http.common.annotation.Query
import io.koraframework.http.common.body.HttpBody
import io.koraframework.http.server.common.annotation.HttpController
import io.koraframework.http.server.common.response.HttpServerResponse
import io.koraframework.json.common.annotation.Json
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@Component
@HttpController
class BenchmarksController(private val repository: WorldRepository) {

    @HttpRoute(method = HttpMethod.GET, path = "/plaintext")
    fun plaintext(): HttpServerResponse {
        return HttpServerResponse.of(200, HttpBody.of("text/plain", PLAINTEXT_RESPONSE))
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/json")
    fun json(): Message = MESSAGE

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/db")
    fun db(): World = repository.findById(QueryUtils.randomWorld())

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/queries")
    fun queries(@Query("queries") queries: String?): List<World> {
        val count = QueryUtils.parseCount(queries)
        val worlds = ArrayList<World>(count)
        repeat(count) {
            worlds.add(repository.findById(QueryUtils.randomWorld()))
        }
        return worlds
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    fun updates(@Query("queries") queries: String?): List<World> {
        val count = QueryUtils.parseCount(queries)
        val worlds = ArrayList<World>(count)
        repeat(count) {
            val id = QueryUtils.randomWorld()
            val oldRandomNumber = repository.findRandomNumberById(id)
            worlds.add(World(id, QueryUtils.randomWorld(oldRandomNumber)))
        }
        worlds.sortBy(World::id)
        repository.update(worlds)
        return worlds
    }

    @HttpRoute(method = HttpMethod.GET, path = "/fortunes")
    fun fortunes(): List<Fortune> {
        val fortunes = repository.fortunes().toMutableList()
        fortunes.add(Fortune(0, "Additional fortune added at request time."))
        fortunes.sortBy(Fortune::message)
        return fortunes
    }

    private companion object {
        val PLAINTEXT_RESPONSE: ByteBuffer = ByteBuffer.wrap("Hello, World!".toByteArray(StandardCharsets.UTF_8))
        val MESSAGE = Message("Hello, World!")
    }
}
