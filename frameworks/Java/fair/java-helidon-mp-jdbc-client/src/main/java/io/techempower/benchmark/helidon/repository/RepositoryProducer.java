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
        var config = Map.ofEntries(
                Map.entry("source", "jdbc"),
                Map.entry("connection.url", url),
                Map.entry("connection.username", username),
                Map.entry("connection.password", password),
                Map.entry("connection.maximumPoolSize", "64"),
                Map.entry("connection.dataSource.preparedStatementCacheQueries", "512"),
                Map.entry("connection.dataSource.preparedStatementCacheSizeMiB", "16"),
                Map.entry("connection.dataSource.prepareThreshold", "1"),
                Map.entry("connection.dataSource.loggerLevel", "OFF"),
                Map.entry("connection.dataSource.sslmode", "disable"),
                Map.entry("connection.dataSource.tcpKeepAlive", "true"),
                Map.entry("connection.dataSource.disableColumnSanitiser", "true")
        );
        return Config.just(() -> ConfigSources.create(config).build());
    }
}
