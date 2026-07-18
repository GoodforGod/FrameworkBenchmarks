package io.micronaut.benchmark.controller;

import io.micronaut.benchmark.model.Fortune;
import io.micronaut.benchmark.model.Message;
import io.micronaut.benchmark.model.World;
import io.micronaut.benchmark.repository.WorldRepository;
import io.micronaut.benchmark.util.QueryUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);

    private final WorldRepository repository;

    public BenchmarksController(WorldRepository repository) {
        this.repository = repository;
    }

    @Get("/plaintext")
    public HttpResponse<byte[]> plaintext() {
        return HttpResponse.ok(PLAINTEXT_RESPONSE)
                .contentType(MediaType.TEXT_PLAIN_TYPE);
    }

    @Get("/json")
    public Message json() {
        return MESSAGE;
    }

    @Get("/db")
    public World db() {
        return repository.findById(QueryUtils.randomWorld());
    }

    @Get("/queries")
    public List<World> queries(@Nullable @QueryValue("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            var world = repository.findById(id);
            worlds.add(world);
        }
        return worlds;
    }

    @Get("/updates")
    public List<World> updates(@Nullable @QueryValue("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            var oldRandomNumber = repository.findRandomNumberById(id);
            var newWorld = new World(id, QueryUtils.randomWorld(oldRandomNumber));
            worlds.add(newWorld);
        }

        worlds.sort(WORLD_COMPARATOR);
        for (World world : worlds) {
            repository.updateRandomNumber(world.id(), world.randomNumber());
        }
        return worlds;
    }

    @Get("/fortunes")
    public List<Fortune> fortunes() {
        List<Fortune> fortunes = repository.fortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));
        fortunes.sort(FORTUNE_COMPARATOR);
        return fortunes;
    }
}
