package io.micronaut.benchmark.jpa.controller;

import io.micronaut.benchmark.jpa.model.Fortune;

import java.util.List;

public final class FortunesModel {

    private final List<Fortune> fortunes;

    public FortunesModel(List<Fortune> fortunes) {
        this.fortunes = fortunes;
    }

    public List<Fortune> getFortunes() {
        return fortunes;
    }
}
