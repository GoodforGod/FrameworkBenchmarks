package io.techempower.benchmark.helidon.repository;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@ApplicationScoped
public class RepositoryProducer {

    @Produces
    @ApplicationScoped
    DbRepository repository(
            @ConfigProperty(name = "POSTGRES_JDBC_URL", defaultValue = "jdbc:postgresql://localhost:5432/postgres") String url,
            @ConfigProperty(name = "POSTGRES_USER", defaultValue = "postgres") String username,
            @ConfigProperty(name = "POSTGRES_PASS", defaultValue = "postgres") String password) {
        return new HelidonDbClientRepository(dbConfig(url, username, password));
    }

    private static Config dbConfig(String url, String username, String password) {
        var config = Map.of(
                "source", "jdbc",
                "connection.url", url,
                "connection.username", username,
                "connection.password", password,
                "connection.maximumPoolSize", "64",
                "connection.dataSource.cachePrepStmts", "true",
                "connection.dataSource.prepStmtCacheSize", "250",
                "connection.dataSource.prepStmtCacheSqlLimit", "2048",
                "connection.dataSource.ssl", "false",
                "connection.dataSource.tcpKeepAlive", "true"
        );
        return Config.just(() -> ConfigSources.create(config).build());
    }
}
