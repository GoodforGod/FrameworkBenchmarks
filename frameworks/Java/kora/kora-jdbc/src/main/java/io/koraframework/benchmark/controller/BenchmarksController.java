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

    @HttpRoute(method = HttpMethod.GET, path = "/plaintext")
    public HttpServerResponse plaintext() {
        return HttpServerResponse.of(200, HttpBody.plaintext(PLAINTEXT_RESPONSE));
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/json")
    public Message json() {
        return MESSAGE;
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/db")
    public World db() {
        return repository.findById(QueryUtils.randomWorld());
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/queries")
    public List<World> queries(@Nullable @Query("queries") Integer queries) {
        int count = QueryUtils.parseCount(queries);
        List<Integer> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(QueryUtils.randomWorld());
        }

        return this.repository.findById(ids);
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "/updates")
    public List<World> updates(@Nullable @Query("queries") Integer queries) {
        return this.repository.getJdbcConnectionFactory().withConnection(() -> {
            int count = QueryUtils.parseCount(queries);
            List<Integer> ids = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                ids.add(QueryUtils.randomWorld());
            }

            //TODO or better call 1 by 1???
            List<World> worlds = repository.findById(ids);
            for (int i = 0; i < worlds.size(); i++) {
                var oldWorld = worlds.get(i);
                var newWorld = new World(oldWorld.id(), QueryUtils.randomWorld());
                worlds.set(i, newWorld);
            }

            worlds.sort(WORLD_COMPARATOR);
            repository.update(worlds);

            return worlds;
        });
    }

    @HttpRoute(method = HttpMethod.GET, path = "/fortunes")
    public List<Fortune> fortunes() {
        List<Fortune> fortunes = repository.fortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));

        fortunes.sort(FORTUNE_COMPARATOR);
        return fortunes;
    }
}
