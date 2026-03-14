package io.koraframework.benchmark

import kotlinx.coroutines.asExecutor
import ru.tinkoff.kora.application.graph.KoraApplication
import ru.tinkoff.kora.common.KoraApp
import ru.tinkoff.kora.common.Tag
import ru.tinkoff.kora.config.hocon.HoconConfigModule
import ru.tinkoff.kora.database.jdbc.JdbcDatabase
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule
import ru.tinkoff.kora.json.module.JsonModule
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@KoraApp
interface Application :
    HoconConfigModule,
    JsonModule,
    JdbcDatabaseModule,
    UndertowHttpServerModule {

    @Tag(JdbcDatabase::class)
    fun jdbcExecutor(): Executor {
        return kotlinx.coroutines.Dispatchers.IO.limitedParallelism(48).asExecutor()
    }
}

fun main() {
    KoraApplication.run { ApplicationGraph.graph() }
}
