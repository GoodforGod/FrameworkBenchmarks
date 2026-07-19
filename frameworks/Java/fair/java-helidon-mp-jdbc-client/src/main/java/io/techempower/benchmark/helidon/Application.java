package io.techempower.benchmark.helidon;

import io.techempower.benchmark.helidon.controller.BenchmarksController;
import io.helidon.logging.common.LogConfig;
import jakarta.ws.rs.ApplicationPath;

import java.util.Set;

@ApplicationPath("/")
public class Application extends jakarta.ws.rs.core.Application {

    public static void main(String[] args) {
        LogConfig.configureRuntime();
        io.helidon.microprofile.cdi.Main.main(args);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(BenchmarksController.class);
    }
}
