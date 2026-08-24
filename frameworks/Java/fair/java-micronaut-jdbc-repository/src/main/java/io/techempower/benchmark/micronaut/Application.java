package io.techempower.benchmark.micronaut;

import io.micronaut.runtime.Micronaut;

public class Application {

    static void main(String[] args) {
        System.out.println("AVAILABLE CORES: " + Runtime.getRuntime().availableProcessors());
        Micronaut.run(Application.class, args);
    }
}
