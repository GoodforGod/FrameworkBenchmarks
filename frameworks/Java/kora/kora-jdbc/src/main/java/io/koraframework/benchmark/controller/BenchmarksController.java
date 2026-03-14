package io.koraframework.benchmark.controller;

import io.koraframework.benchmark.model.Fortune;
import io.koraframework.benchmark.model.Message;
import io.koraframework.benchmark.model.World;
import io.koraframework.benchmark.repository.WorldRepository;
import io.koraframework.benchmark.util.QueryUtils;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.http.common.HttpMethod;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.Query;
import ru.tinkoff.kora.http.common.body.HttpBody;
import ru.tinkoff.kora.http.server.common.HttpServerResponse;
import ru.tinkoff.kora.http.server.common.annotation.HttpController;
import ru.tinkoff.kora.json.common.annotation.Json;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return HttpServerResponse.of(200, HttpBody.plaintext(PLAINTEXT_RESPONSE));
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
        Set<Integer> ids = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            int nextId = QueryUtils.randomWorld();
            if (!ids.add(nextId)) {
                QueryUtils.addNextRandomWorld(ids, nextId);
            }
        }

        return this.repository.findById(ids);
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    public List<World> updates(@Nullable @Query("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        Set<Integer> ids = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            int nextId = QueryUtils.randomWorld();
            if (!ids.add(nextId)) {
                QueryUtils.addNextRandomWorld(ids, nextId);
            }
        }

        var result = this.repository.getJdbcConnectionFactory().withConnection(() -> {
            //TODO or better call 1 by 1???
            List<World> worlds = repository.findById(ids);
            for (int i = 0; i < worlds.size(); i++) {
                var oldWorld = worlds.get(i);
                var newWorld = new World(oldWorld.id(), QueryUtils.randomWorld());
                worlds.set(i, newWorld);
            }

            repository.update(worlds);
            return worlds;
        });

        result.sort(WORLD_COMPARATOR);
        return result;
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
