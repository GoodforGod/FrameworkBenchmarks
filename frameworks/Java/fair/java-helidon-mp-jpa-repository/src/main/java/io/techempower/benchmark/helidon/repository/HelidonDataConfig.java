package io.techempower.benchmark.helidon.repository;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.service.registry.Services;

import java.util.Map;

public final class HelidonDataConfig {

    private HelidonDataConfig() {
    }

    public static void configure() {
        Services.set(Config.class, dataConfig(
                env("POSTGRES_JDBC_URL", "jdbc:postgresql://localhost:5432/postgres"),
                env("POSTGRES_USER", "postgres"),
                env("POSTGRES_PASS", "postgres")
        ));
    }

    private static Config dataConfig(String url, String username, String password) {
        var config = Map.ofEntries(
                Map.entry("data.persistence-units.jakarta.0.name", "@default"),
                Map.entry("data.persistence-units.jakarta.0.provider-class-name", "org.hibernate.jpa.HibernatePersistenceProvider"),
                Map.entry("data.persistence-units.jakarta.0.connection.url", url),
                Map.entry("data.persistence-units.jakarta.0.connection.username", username),
                Map.entry("data.persistence-units.jakarta.0.connection.password", password),
                Map.entry("data.persistence-units.jakarta.0.connection.jdbc-driver-class-name", "org.postgresql.Driver"),
                Map.entry("data.persistence-units.jakarta.0.properties.jakarta.persistence.jdbc.driver", "org.postgresql.Driver"),
                Map.entry("data.persistence-units.jakarta.0.properties.jakarta.persistence.jdbc.url", url),
                Map.entry("data.persistence-units.jakarta.0.properties.jakarta.persistence.jdbc.user", username),
                Map.entry("data.persistence-units.jakarta.0.properties.jakarta.persistence.jdbc.password", password),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.connection.provider_disables_autocommit", "true"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.hikari.maximumPoolSize", "64"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.hikari.dataSource.cachePrepStmts", "true"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.hikari.dataSource.prepStmtCacheSize", "250"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.hikari.dataSource.ssl", "false"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.hikari.dataSource.tcpKeepAlive", "true"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.show_sql", "false"),
                Map.entry("data.persistence-units.jakarta.0.properties.hibernate.format_sql", "false")
        );
        return Config.just(() -> ConfigSources.create(config).build());
    }

    private static String env(String name, String defaultValue) {
        var value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
