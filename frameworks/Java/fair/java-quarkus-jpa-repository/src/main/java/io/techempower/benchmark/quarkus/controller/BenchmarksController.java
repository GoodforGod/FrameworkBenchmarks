package io.techempower.benchmark.quarkus.controller;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.techempower.benchmark.quarkus.model.Message;
import io.techempower.benchmark.quarkus.model.World;
import io.techempower.benchmark.quarkus.repository.FortuneRepository;
import io.techempower.benchmark.quarkus.repository.WorldRepository;
import io.techempower.benchmark.quarkus.util.JteUtils;
import io.techempower.benchmark.quarkus.util.QueryUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Path("/")
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(f -> f.message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(w -> w.randomNumber);

    private final WorldRepository worldRepository;
    private final FortuneRepository fortuneRepository;

    @Inject
    public BenchmarksController(WorldRepository worldRepository, FortuneRepository fortuneRepository) {
        this.worldRepository = worldRepository;
        this.fortuneRepository = fortuneRepository;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @GET
    @Path("/plaintext")
    @Produces(MediaType.TEXT_PLAIN)
    public Response plaintext() {
        return Response.ok(PLAINTEXT_RESPONSE)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#json-serialization
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Message json() {
        return MESSAGE;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#single-database-query
    @GET
    @Path("/db")
    @Produces(MediaType.APPLICATION_JSON)
    public World db() {
        return worldRepository.findWorld(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> queries(@QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            World world = worldRepository.findWorld(id);
            worlds.add(world);
        }

        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @GET
    @Path("/updates")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> updates(@QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            World world = worldRepository.findWorld(id);
            world.randomNumber = QueryUtils.randomWorld();
            worldRepository.persist(world);
            worlds.add(world);
        }

        worlds.sort(WORLD_COMPARATOR);
        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @GET
    @Path("/fortunes")
    @Produces(MediaType.TEXT_HTML)
    public Response fortunes() {
        List<Fortune> fortunes = fortuneRepository.listAll();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));

        fortunes.sort(FORTUNE_COMPARATOR);

        return Response.ok(JteUtils.serializeStandard(fortunes))
                .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                .build();
    }
}
