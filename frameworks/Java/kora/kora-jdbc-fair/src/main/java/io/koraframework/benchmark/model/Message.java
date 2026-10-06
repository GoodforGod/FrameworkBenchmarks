package io.koraframework.benchmark.model;

import io.koraframework.json.common.annotation.Json;

@Json
public record Message(String message) {
}
