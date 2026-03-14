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
import java.nio.ByteBuffer

@Module
interface JteModule {

    fun jteListHttpServerResponseMapper(): HttpServerResponseMapper<List<Fortune>> {
        return HttpServerResponseMapper { _, _, result ->
            val out = Utf8ByteOutput()
            val output: HtmlTemplateOutput = OwaspHtmlTemplateOutput(out)
            JtefortunesGenerated.render(output, null, result)
            val buffer = ByteBuffer.allocate(out.contentLength)
            out.writeTo(buffer::put)
            HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, buffer.flip()))
        }
    }

    private companion object {
        const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }
}
