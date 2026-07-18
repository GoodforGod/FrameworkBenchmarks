package org.springframework.benchmark.jdbc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(@JsonProperty("message") String message) {
}
