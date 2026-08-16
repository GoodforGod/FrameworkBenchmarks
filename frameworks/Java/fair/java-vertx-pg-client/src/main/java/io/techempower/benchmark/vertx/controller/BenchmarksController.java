package io.techempower.benchmark.vertx.controller;

import io.techempower.benchmark.vertx.model.Fortune;
import io.techempower.benchmark.vertx.model.World;
import io.techempower.benchmark.vertx.repository.WorldRepository;
import io.techempower.benchmark.vertx.util.JteUtils;
import io.techempower.benchmark.vertx.util.QueryUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

public final class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    private static final CharSequence HELLO_WORLD_LENGTH = HttpHeaders.createOptimized("" + PLAINTEXT_RESPONSE.length);
    private static final CharSequence CONTENT_TEXT = HttpHeaders.createOptimized("text/plain");
    private static final CharSequence CONTENT_JSON = HttpHeaders.createOptimized("application/json");
    private static final CharSequence CONTENT_HTML = HttpHeaders.createOptimized("text/html; charset=utf-8");

    private static final JsonObject MESSAGE_JSON = new JsonObject().put("message", "Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::getMessage);

    private final WorldRepository worldRepository;

    public BenchmarksController(WorldRepository worldRepository) {
        this.worldRepository = worldRepository;
    }

    public void mount(Router router) {
        router.get("/plaintext").handler(this::plaintext);
        router.get("/json").handler(this::json);
        router.get("/db").handler(this::db);
        router.get("/queries").handler(this::queries);
        router.get("/updates").handler(this::updates);
        router.get("/fortunes").handler(this::fortunes);
    }

    public void plaintext(RoutingContext context) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TEXT)
                .putHeader(HttpHeaders.CONTENT_LENGTH, HELLO_WORLD_LENGTH)
                .end(Buffer.buffer(PLAINTEXT_RESPONSE));
    }

    public void json(RoutingContext context) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_JSON)
                .end(MESSAGE_JSON.toBuffer());
    }

    public void db(RoutingContext context) {
        worldRepository.findWorld(QueryUtils.randomWorld())
                .onSuccess(world -> json(context, world))
                .onFailure(error -> fail(context, error));
    }

    public void queries(RoutingContext context) {
        int count = QueryUtils.parseCount(context.request().getParam("queries"));
        worldRepository.findWorlds(count)
                .onSuccess(worlds -> json(context, worlds))
                .onFailure(error -> fail(context, error));
    }

    public void updates(RoutingContext context) {
        int count = QueryUtils.parseCount(context.request().getParam("queries"));
        worldRepository.updateWorlds(count)
                .onSuccess(worlds -> json(context, worlds))
                .onFailure(error -> fail(context, error));
    }

    public void fortunes(RoutingContext context) {
        worldRepository.findFortunes()
                .onSuccess(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(FORTUNE_COMPARATOR);
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_HTML)
                            .end(Buffer.buffer(JteUtils.serializeStandard(fortunes)));
                })
                .onFailure(error -> fail(context, error));
    }

    private static void json(RoutingContext context, World world) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_JSON)
                .end(toJson(world).encode());
    }

    private static void json(RoutingContext context, List<World> worlds) {
        JsonArray json = new JsonArray();
        for (World world : worlds) {
            json.add(toJson(world));
        }
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_JSON)
                .end(json.encode());
    }

    private static JsonObject toJson(World world) {
        return new JsonObject()
                .put("id", world.getId())
                .put("randomNumber", world.getRandomNumber());
    }

    private static void fail(RoutingContext context, Throwable error) {
        error.printStackTrace();
        if (!context.response().ended()) {
            context.response().setStatusCode(500).end();
        }
    }
}
