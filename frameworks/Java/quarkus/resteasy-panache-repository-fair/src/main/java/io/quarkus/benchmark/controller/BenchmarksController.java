package io.quarkus.benchmark.controller;

import io.quarkus.benchmark.model.Message;
import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.repository.WorldRepository;
import io.quarkus.benchmark.util.WorldUtils;
import io.smallrye.common.annotation.NonBlocking;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/")
public final class BenchmarksController {

    private static final Message MESSAGE = new Message("Hello, World!");
    private static final Buffer HELLO_WORLD_BUFFER = Buffer.buffer("Hello, world!".getBytes(StandardCharsets.UTF_8));

    @Inject
    private WorldRepository repository;

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/plaintext")
    @GET
    @NonBlocking
    public Buffer plaintext() {
        return HELLO_WORLD_BUFFER;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#json-serialization
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/json")
    @GET
    @NonBlocking
    public Message json() {
        return MESSAGE;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#single-database-query
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/db")
    @NonBlocking
    public World db() {
        return repository.findById(WorldUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/queries")
    @NonBlocking
    public List<World> queries(@QueryParam("queries") String queries) {
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
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/updates")
    @NonBlocking
    @Transactional
    public List<World> updates(@QueryParam("queries") String queries) {
        int amount = WorldUtils.parseAmount(queries);
        List<World> result = new ArrayList<>(amount);
        var randomIds = WorldUtils.randomWorlds(amount);

        for (int randomId : randomIds) {
            var oldWorld = repository.findById(randomId);
            oldWorld.randomNumber = WorldUtils.randomWorld(oldWorld.randomNumber);
            result.add(oldWorld);
        }

        return result;
    }
}
