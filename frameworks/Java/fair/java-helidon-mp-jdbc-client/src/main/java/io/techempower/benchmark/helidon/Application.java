package io.techempower.benchmark.helidon;

import io.helidon.logging.common.LogConfig;
import io.helidon.microprofile.cdi.Main;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Application extends jakarta.ws.rs.core.Application {

    static void main(String[] args) {
        LogConfig.configureRuntime();
        Main.main(args);
    }
}
