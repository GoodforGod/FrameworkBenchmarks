package io.koraframework.benchmark.model;

import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record Message(String message) {
}
