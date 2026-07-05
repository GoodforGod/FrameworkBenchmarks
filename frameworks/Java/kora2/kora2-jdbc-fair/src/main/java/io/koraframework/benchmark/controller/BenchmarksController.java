package io.koraframework.benchmark.controller;

import io.koraframework.benchmark.model.Message;
import io.koraframework.benchmark.model.World;
import io.koraframework.benchmark.repository.WorldRepository;
import io.koraframework.benchmark.util.WorldUtils;
import io.koraframework.common.Component;
import io.koraframework.http.common.HttpMethod;
import io.koraframework.http.common.annotation.HttpRoute;
import io.koraframework.http.common.annotation.Query;
import io.koraframework.http.server.common.annotation.HttpController;
import io.koraframework.json.common.annotation.Json;
import org.jspecify.annotations.Nullable;

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

    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);

    private final WorldRepository repository;

    public BenchmarksController(WorldRepository repository) {
        this.repository = repository;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @HttpRoute(method = HttpMethod.GET, path = "/plaintext")
    public ByteBuffer plaintext() {
        return PLAINTEXT_RESPONSE;
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
        return repository.findById(WorldUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/queries")
    public List<World> queries(@Nullable @Query("queries") String queries) {
        int amount = WorldUtils.parseAmount(queries);
        List<World> result = new ArrayList<>(amount);
        var randomIds = WorldUtils.randomWorlds(amount);

        for (int randomId : randomIds) {
            var world = repository.findById(randomId);
            result.add(world);
        }

        return result;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    public List<World> updates(@Nullable @Query("queries") String queries) {
        int amount = WorldUtils.parseAmount(queries);
        List<World> result = new ArrayList<>(amount);
        var randomIds = WorldUtils.randomWorlds(amount);
        for (int randomId : randomIds) {
            var oldRandomNumber = repository.findRandomNumberById(randomId);
            var newWorld = new World(randomId, WorldUtils.randomWorld(oldRandomNumber));
            result.add(newWorld);
        }

        result.sort(WORLD_COMPARATOR);
        repository.update(result);
        return result;
    }
}
