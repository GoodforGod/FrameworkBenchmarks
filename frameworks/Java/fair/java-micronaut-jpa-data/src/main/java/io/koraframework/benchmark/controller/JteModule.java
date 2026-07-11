package io.koraframework.benchmark.controller;

import io.koraframework.benchmark.model.Fortune;
import io.koraframework.benchmark.util.JteUtils;
import io.koraframework.common.Module;
import io.koraframework.http.common.body.HttpBody;
import io.koraframework.http.server.common.response.HttpServerResponse;
import io.koraframework.http.server.common.response.HttpServerResponseMapper;

import java.util.List;

@Module
public interface JteModule {

    String CONTENT_TYPE = "text/html;charset=UTF-8";

    default HttpServerResponseMapper<List<Fortune>> jteListHttpServerResponseMapper() {
        return Boolean.parseBoolean(System.getenv("JET_CUSTOM"))
                ? (request, result) -> HttpServerResponse.of(200, request.headers(), HttpBody.of(CONTENT_TYPE, JteUtils.serializeCustom(result)))
                : (request, result) -> HttpServerResponse.of(200, request.headers(), HttpBody.of(CONTENT_TYPE, JteUtils.serializeStandard(result)));
    }
}
