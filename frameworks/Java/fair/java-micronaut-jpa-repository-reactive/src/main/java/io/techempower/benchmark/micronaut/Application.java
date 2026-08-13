package io.techempower.benchmark.micronaut;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;
import jakarta.persistence.Entity;

@Introspected(packages = "io.techempower.benchmark.micronaut.model", includedAnnotations = Entity.class)
public final class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
