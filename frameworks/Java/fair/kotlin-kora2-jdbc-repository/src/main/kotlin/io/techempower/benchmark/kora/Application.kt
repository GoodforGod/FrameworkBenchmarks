package io.techempower.benchmark.kora

import io.koraframework.application.graph.KoraApplication
import io.koraframework.common.annotation.KoraApp
import io.koraframework.config.hocon.HoconConfigModule
import io.koraframework.database.jdbc.JdbcDatabaseModule
import io.koraframework.http.server.undertow.UndertowPublicHttpServerModule
import io.koraframework.json.common.JsonModule

@KoraApp
interface Application : HoconConfigModule,
    JsonModule,
    JdbcDatabaseModule,
    UndertowPublicHttpServerModule

fun main() {
    println("AVAILABLE CORES: ${Runtime.getRuntime().availableProcessors()}")
    KoraApplication.run { ApplicationGraph.graph() }
}
