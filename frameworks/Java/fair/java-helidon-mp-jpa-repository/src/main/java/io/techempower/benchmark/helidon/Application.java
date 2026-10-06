package io.techempower.benchmark.helidon;

import io.techempower.benchmark.helidon.repository.HelidonDataConfig;
import io.helidon.logging.common.LogConfig;
import io.helidon.microprofile.cdi.Main;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends jakarta.ws.rs.core.Application {

    static void main(String[] args) {
        System.out.println("AVAILABLE CORES: " + Runtime.getRuntime().availableProcessors());
        LogConfig.configureRuntime();
        HelidonDataConfig.configure();
        Main.main(args);
    }
}
