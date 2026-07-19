package io.techempower.benchmark.helidon.repository;

import io.helidon.config.Config;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class RepositoryProducer {

    @Produces
    @ApplicationScoped
    DbRepository repository() {
        return new HelidonDbClientRepository(Config.create().get("db"));
    }
}
