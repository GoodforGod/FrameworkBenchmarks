package io.techempower.benchmark.kora.controller

import io.techempower.benchmark.kora.model.Fortune
import io.techempower.benchmark.kora.util.JteUtils
import io.koraframework.common.annotation.Module
import io.koraframework.http.common.body.HttpBody
import io.koraframework.http.server.common.response.HttpServerResponse
import io.koraframework.http.server.common.response.HttpServerResponseMapper

@Module
interface JteModule {

    fun jteListHttpServerResponseMapper(): HttpServerResponseMapper<List<Fortune>> {
        return HttpServerResponseMapper { request, result ->
            HttpServerResponse.of(200, request.headers(), HttpBody.of("text/html;charset=UTF-8", JteUtils.serializeStandard(result!!)))
        }
    }
}
