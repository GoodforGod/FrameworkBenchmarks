package io.techempower.benchmark.kora.controller;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.http.common.HttpMethod;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.Query;
import ru.tinkoff.kora.http.common.body.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.annotation.HttpController;
import ru.tinkoff.kora.json.common.annotation.Json;
import io.techempower.benchmark.kora.model.Fortune;
import io.techempower.benchmark.kora.model.Message;
import io.techempower.benchmark.kora.model.World;
import io.techempower.benchmark.kora.repository.WorldRepository;
import io.techempower.benchmark.kora.util.QueryUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@HttpController
public final class BenchmarksController {

    private static final ByteBuffer PLAINTEXT_RESPONSE = ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8));
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);

    private final WorldRepository repository;

    public BenchmarksController(WorldRepository repository) {
        this.repository = repository;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @HttpRoute(method = HttpMethod.GET, path = "/plaintext")
    public HttpServerResponse plaintext() {
        return HttpServerResponse.of(200, HttpBody.of("text/plain", PLAINTEXT_RESPONSE));
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#json-serialization
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/json")
    public Message json() {
        return MESSAGE;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#single-database-query
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/db")
    public World db() {
        return repository.findById(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/queries")
    public List<World> queries(@Nullable @Query("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            var world = repository.findById(id);
            worlds.add(world);
        }

        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    public List<World> updates(@Nullable @Query("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            var oldRandomNumber = repository.findRandomNumberById(id);
            var newWorld = new World(id, QueryUtils.randomWorld(oldRandomNumber));
            worlds.add(newWorld);
        }

        worlds.sort(WORLD_COMPARATOR);
        repository.update(worlds);
        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @HttpRoute(method = HttpMethod.GET, path = "/fortunes")
    public List<Fortune> fortunes() {
        List<Fortune> fortunes = repository.fortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));

        fortunes.sort(FORTUNE_COMPARATOR);
        return fortunes;
    }
}
