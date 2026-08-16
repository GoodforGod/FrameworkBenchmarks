package io.techempower.benchmark.kora

import ru.tinkoff.kora.application.graph.KoraApplication
import ru.tinkoff.kora.common.KoraApp
import ru.tinkoff.kora.config.hocon.HoconConfigModule
import ru.tinkoff.kora.database.vertx.VertxDatabaseModule
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule
import ru.tinkoff.kora.json.module.JsonModule

@KoraApp
interface Application : HoconConfigModule,
    JsonModule,
    VertxDatabaseModule,
    UndertowHttpServerModule

fun main() {
    KoraApplication.run { ApplicationGraph.graph() }
}
