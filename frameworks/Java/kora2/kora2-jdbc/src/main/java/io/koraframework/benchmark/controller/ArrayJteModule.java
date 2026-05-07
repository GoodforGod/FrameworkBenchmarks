package io.koraframework.benchmark.controller;

import gg.jte.generated.precompiled.JtefortunesGenerated;
import gg.jte.html.HtmlTemplateOutput;
import gg.jte.html.OwaspHtmlTemplateOutput;
import io.koraframework.benchmark.model.Fortune;
import io.koraframework.common.DefaultComponent;
import io.koraframework.common.Module;
import io.koraframework.http.common.body.HttpBody;
import io.koraframework.http.server.common.response.HttpServerResponse;
import io.koraframework.http.server.common.response.HttpServerResponseMapper;

import java.util.List;

@Module
public interface ArrayJteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    @DefaultComponent
    default HttpServerResponseMapper<List<Fortune>> fastJteListHttpServerResponseMapper() {
        return (request, result) -> {
            var out = new ArrayUtf8ByteOutput(256);
            HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
            JtefortunesGenerated.render(template, null, result);
            var response = out.buffer();
            return HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, response));
        };
    }
}
