package io.techempower.benchmark.quarkus.controller;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.techempower.benchmark.quarkus.model.Message;
import io.techempower.benchmark.quarkus.model.World;
import io.techempower.benchmark.quarkus.repository.FortuneRepository;
import io.techempower.benchmark.quarkus.repository.WorldRepository;
import io.techempower.benchmark.quarkus.util.JteUtils;
import io.techempower.benchmark.quarkus.util.QueryUtils;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
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
    @WithSession
    public Uni<World> db() {
        return worldRepository.findWorld(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<List<World>> queries(@QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Multi.createFrom().range(0, count)
                .onItem().transformToUniAndConcatenate(ignored -> worldRepository.findWorld(QueryUtils.randomWorld()))
                .collect().asList();
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @GET
    @Path("/updates")
    @Produces(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<List<World>> updates(@QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Multi.createFrom().range(0, count)
                .onItem().transformToUniAndConcatenate(ignored -> worldRepository.findWorld(QueryUtils.randomWorld())
                        .map(world -> {
                            // Keep this endpoint in the reactive Panache repository style: load a
                            // managed entity, read the current value required by TechEmpower, mutate it,
                            // and let Hibernate Reactive flush dirty entities inside the transaction.
                            world.randomNumber = QueryUtils.randomWorld(world.randomNumber);
                            return world;
                        }))
                .collect().asList()
                .map(worlds -> worlds.stream().sorted(WORLD_COMPARATOR).toList());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @GET
    @Path("/fortunes")
    @Produces(MediaType.TEXT_HTML)
    @WithSession
    public Uni<Response> fortunes() {
        return fortuneRepository.listAll()
                .map(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(FORTUNE_COMPARATOR);
                    return Response.ok(JteUtils.serializeStandard(fortunes))
                            .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                            .build();
                });
    }
}
