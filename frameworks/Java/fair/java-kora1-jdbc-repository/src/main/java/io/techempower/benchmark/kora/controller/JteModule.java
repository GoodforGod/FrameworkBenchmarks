package io.techempower.benchmark.kora.controller;

import io.techempower.benchmark.kora.model.Fortune;
import io.techempower.benchmark.kora.util.JteUtils;
import ru.tinkoff.kora.common.Module;
import ru.tinkoff.kora.http.common.body.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.handler.HttpServerResponseMapper;

import java.util.List;

@Module
public interface JteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    default HttpServerResponseMapper<List<Fortune>> jteListHttpServerResponseMapper() {
        return (ctx, request, result) -> HttpServerResponse.of(200, request.headers(), HttpBody.of(CONTENT_TYPE, JteUtils.serializeStandard(result)));
    }
}
