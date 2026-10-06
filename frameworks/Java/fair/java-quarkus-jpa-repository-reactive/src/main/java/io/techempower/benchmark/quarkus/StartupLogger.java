package io.techempower.benchmark.quarkus;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public final class StartupLogger {

    void onStart(@Observes StartupEvent event) {
        System.out.println("AVAILABLE CORES: " + Runtime.getRuntime().availableProcessors());
    }
}
