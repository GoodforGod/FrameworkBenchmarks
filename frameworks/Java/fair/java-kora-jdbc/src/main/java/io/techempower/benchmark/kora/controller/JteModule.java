package io.techempower.benchmark.kora.controller;

import io.techempower.benchmark.kora.model.Fortune;
import io.techempower.benchmark.kora.util.JteUtils;
import io.koraframework.common.Module;
import io.koraframework.http.common.body.HttpBody;
import io.koraframework.http.server.common.response.HttpServerResponse;
import io.koraframework.http.server.common.response.HttpServerResponseMapper;

import java.util.List;

@Module
public interface JteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    default HttpServerResponseMapper<List<Fortune>> jteListHttpServerResponseMapper() {
        return(request, result) -> HttpServerResponse.of(200, request.headers(), HttpBody.of(CONTENT_TYPE, JteUtils.serializeStandard(result)));
    }
}
