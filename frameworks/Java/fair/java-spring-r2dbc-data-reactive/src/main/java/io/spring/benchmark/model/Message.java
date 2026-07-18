package io.spring.benchmark.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty("message")
    private final String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
