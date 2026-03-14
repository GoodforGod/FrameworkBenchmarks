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

import java.util.List;

@Module
public interface JteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    default HttpServerResponseMapper<List<Fortune>> jteListHttpServerResponseMapper() {
        return (ctx, request, result) -> {
            var out = new Utf8ByteOutput(64);
            HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
            JtefortunesGenerated.render(template, null, result);
            var response = out.toByteArray();
            return HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, response));
        };
    }
}
