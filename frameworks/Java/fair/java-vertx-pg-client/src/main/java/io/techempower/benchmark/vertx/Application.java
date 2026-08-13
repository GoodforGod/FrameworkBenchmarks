package io.techempower.benchmark.vertx;

import io.techempower.benchmark.vertx.controller.BenchmarksController;
import io.techempower.benchmark.vertx.repository.WorldRepository;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetClientOptions;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public final class Application {

    private static final int HTTP_PORT = 8080;

    static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Pool pool = createPgPool(vertx);
        Router router = Router.router(vertx);

        new BenchmarksController(new WorldRepository(pool)).mount(router);

        vertx.createHttpServer(new HttpServerOptions()
                        .setPort(HTTP_PORT)
                        .setTcpNoDelay(true)
                        .setCompressionSupported(false))
                .requestHandler(router)
                .listen(HTTP_PORT)
                .onFailure(error -> {
                    error.printStackTrace();
                    vertx.close();
                });
    }

    private static Pool createPgPool(Vertx vertx) {
        String uri = env("POSTGRES_REACTIVE_URL", "postgresql://localhost:5432/postgres");
        PgConnectOptions connectOptions = PgConnectOptions.fromUri(uri)
                .setUser(env("POSTGRES_USER", "postgres"))
                .setPassword(env("POSTGRES_PASS", "postgres"))
                .setPipeliningLimit(envInt("POSTGRES_PIPELINING_LIMIT", 1))
                .setCachePreparedStatements(true)
                .setPreparedStatementCacheMaxSize(256)
                .setPreparedStatementCacheSqlLimit(2048);
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(512)
                .setMaxWaitQueueSize(-1)
                .setConnectionTimeout(10_000)
                .setIdleTimeout(0)
                .setMaxLifetime(0);
        NetClientOptions netClientOptions = new NetClientOptions()
                .setTcpNoDelay(true)
                .setTcpKeepAlive(true)
                .setConnectTimeout(10_000);
        return PgBuilder.pool()
                .using(vertx)
                .connectingTo(connectOptions)
                .with(poolOptions)
                .with(netClientOptions)
                .build();
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int envInt(String name, int fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
