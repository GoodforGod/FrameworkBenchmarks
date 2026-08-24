package io.techempower.benchmark.vertx;

import io.techempower.benchmark.vertx.controller.BenchmarksController;
import io.techempower.benchmark.vertx.repository.WorldRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetClientOptions;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class Application extends AbstractVerticle {

    private static final int HTTP_PORT = 8080;
    private static volatile String dateHeader = formatDateHeader();

    private final Pool pool;

    private Application(Pool pool) {
        this.pool = pool;
    }

    public static void main(String[] args) {
        System.out.println("AVAILABLE CORES: " + Runtime.getRuntime().availableProcessors());
        Vertx vertx = Vertx.vertx();
        Pool pool = createPgPool(vertx);
        vertx.setPeriodic(1_000, ignored -> dateHeader = formatDateHeader());
        int instances = Math.max(1, Runtime.getRuntime().availableProcessors());
        vertx.deployVerticle(() -> new Application(pool), new DeploymentOptions().setInstances(instances))
                .onFailure(error -> {
                    error.printStackTrace();
                    pool.close();
                    vertx.close();
                });
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.route().handler(context -> {
            context.response()
                    .putHeader("Server", "Vert.x")
                    .putHeader("Date", dateHeader);
            context.next();
        });
        new BenchmarksController(new WorldRepository(pool)).mount(router);

        vertx.createHttpServer(new HttpServerOptions()
                        .setPort(HTTP_PORT)
                        .setTcpNoDelay(true)
                        .setCompressionSupported(false))
                .requestHandler(router)
                .listen(HTTP_PORT)
                .onSuccess(server -> startPromise.complete())
                .onFailure(startPromise::fail);
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
                .setConnectionTimeout(20_000)
                .setIdleTimeout(0)
                .setMaxLifetime(0);
        NetClientOptions netClientOptions = new NetClientOptions()
                .setTcpNoDelay(true)
                .setTcpKeepAlive(true)
                .setConnectTimeout(20_000);
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

    private static String formatDateHeader() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
