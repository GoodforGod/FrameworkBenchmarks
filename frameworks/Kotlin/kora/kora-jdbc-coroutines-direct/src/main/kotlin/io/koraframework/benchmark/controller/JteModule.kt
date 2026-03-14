package io.koraframework.benchmark.controller

import gg.jte.generated.precompiled.JtefortunesGenerated
import gg.jte.html.HtmlTemplateOutput
import gg.jte.html.OwaspHtmlTemplateOutput
import gg.jte.output.Utf8ByteOutput
import io.koraframework.benchmark.model.Fortune
import ru.tinkoff.kora.common.Module
import ru.tinkoff.kora.http.common.body.HttpBody
import ru.tinkoff.kora.http.server.common.HttpServerResponse
import ru.tinkoff.kora.http.server.common.handler.HttpServerResponseMapper

@Module
interface JteModule {

    private companion object {
        const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }

    fun jteListHttpServerResponseMapper(): HttpServerResponseMapper<List<Fortune>> {
        return HttpServerResponseMapper { _, _, result ->
            val out = Utf8ByteOutput(64)
            val template = OwaspHtmlTemplateOutput(out)
            JtefortunesGenerated.render(template, null, result)
            val response = out.toByteArray()
            HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, response))
        }
    }
}
