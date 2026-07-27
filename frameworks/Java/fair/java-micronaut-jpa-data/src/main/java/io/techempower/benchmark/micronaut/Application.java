package io.techempower.benchmark.micronaut;

import io.micronaut.runtime.Micronaut;

public final class Application {

    static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
