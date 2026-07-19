package io.techempower.benchmark.spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record World(@JsonProperty("id") int id, 
                    @JsonProperty("randomNumber") int randomNumber) {
}
