package io.koraframework.benchmark.controller;

import gg.jte.generated.precompiled.JtefortunesGenerated;
import gg.jte.html.HtmlTemplateOutput;
import gg.jte.html.OwaspHtmlTemplateOutput;
import gg.jte.output.Utf8ByteOutput;
import io.koraframework.benchmark.model.Fortune;
import io.koraframework.common.Module;
import io.koraframework.http.common.body.HttpBody;
import io.koraframework.http.server.common.response.HttpServerResponse;
import io.koraframework.http.server.common.response.HttpServerResponseMapper;

import java.util.List;

@Module
public interface JteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    default HttpServerResponseMapper<List<Fortune>> jteListHttpServerResponseMapper() {
        if(Boolean.parseBoolean(System.getenv("JET_FAST"))) {
            return (request, result) -> {
                var out = new ArrayUtf8ByteOutput(256);
                HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
                JtefortunesGenerated.render(template, null, result);
                var response = out.buffer();
                return HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, response));
            };
        } else {
            return (request, result) -> {
                var out = new Utf8ByteOutput(64);
                HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
                JtefortunesGenerated.render(template, null, result);
                var response = out.toByteArray();
                return HttpServerResponse.of(200, HttpBody.of(CONTENT_TYPE, response));
            };
        }
    }
}
