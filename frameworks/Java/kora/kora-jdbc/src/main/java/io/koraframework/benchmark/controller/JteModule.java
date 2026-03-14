package io.koraframework.benchmark.controller;

import gg.jte.generated.precompiled.JtefortunesGenerated;
import gg.jte.html.HtmlTemplateOutput;
import gg.jte.html.OwaspHtmlTemplateOutput;
import gg.jte.output.Utf8ByteOutput;
import io.koraframework.benchmark.model.Fortune;
import ru.tinkoff.kora.common.Module;
import ru.tinkoff.kora.http.common.body.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.handler.HttpServerResponseMapper;

import java.nio.ByteBuffer;
import java.util.List;

@Module
public interface JteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    default HttpServerResponseMapper<List<Fortune>> jteListHttpServerResponseMapper() {
        return (ctx, request, result) -> {
            var out = new Utf8ByteOutput();
            HtmlTemplateOutput _output = new OwaspHtmlTemplateOutput(out);
            JtefortunesGenerated.render(_output, null, result);
            var buf = ByteBuffer.allocate(out.getContentLength());
            out.writeTo(buf::put);
            return HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, buf.flip()));
        };
    }
}
