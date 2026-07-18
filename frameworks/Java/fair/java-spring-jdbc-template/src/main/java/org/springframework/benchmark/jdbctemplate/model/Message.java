package org.springframework.benchmark.jdbctemplate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(@JsonProperty("message") String message) {
}
