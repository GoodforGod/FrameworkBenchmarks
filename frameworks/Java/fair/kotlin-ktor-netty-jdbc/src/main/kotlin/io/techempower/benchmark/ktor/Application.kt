package io.techempower.benchmark.ktor

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.techempower.benchmark.ktor.model.Fortune
import io.techempower.benchmark.ktor.model.Message
import io.techempower.benchmark.ktor.repository.WorldRepository
import io.techempower.benchmark.ktor.util.JteUtils
import io.techempower.benchmark.ktor.util.QueryUtils
import kotlinx.serialization.json.Json

private val MESSAGE = Message("Hello, World!")
private val repository = WorldRepository()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        benchmarks()
    }.start(wait = true)
}

fun Application.benchmarks() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    routing {
        get("/plaintext") {
            call.response.headers.append(HttpHeaders.Server, "Ktor")
            call.respondText("Hello, World!", ContentType.Text.Plain, HttpStatusCode.OK)
        }
        get("/json") {
            call.respond(MESSAGE)
        }
        get("/db") {
            call.respond(repository.findWorld(QueryUtils.randomWorld()))
        }
        get("/queries") {
            call.respond(repository.findWorlds(QueryUtils.parseCount(call.request.queryParameters["queries"])))
        }
        get("/updates") {
            call.respond(repository.updateWorlds(QueryUtils.parseCount(call.request.queryParameters["queries"])))
        }
        get("/fortunes") {
            val fortunes = repository.findFortunes().toMutableList()
            fortunes.add(Fortune(0, "Additional fortune added at request time."))
            fortunes.sortWith(compareBy(Fortune::message))
            call.respondBytes(JteUtils.serializeStandard(fortunes), ContentType.parse("text/html; charset=utf-8"))
        }
    }
}
