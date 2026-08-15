package io.techempower.benchmark.kora.controller

import io.techempower.benchmark.kora.model.Fortune
import io.techempower.benchmark.kora.util.JteUtils
import ru.tinkoff.kora.common.Module
import ru.tinkoff.kora.http.common.body.HttpBody
import ru.tinkoff.kora.http.server.common.HttpServerResponse
import ru.tinkoff.kora.http.server.common.handler.HttpServerResponseMapper

@Module
interface JteModule {

    fun jteListHttpServerResponseMapper(): HttpServerResponseMapper<List<Fortune>> {
        return HttpServerResponseMapper { _, request, result ->
            HttpServerResponse.of(200, request.headers(), HttpBody.of("text/html;charset=UTF-8", JteUtils.serializeStandard(result)))
        }
    }
}
