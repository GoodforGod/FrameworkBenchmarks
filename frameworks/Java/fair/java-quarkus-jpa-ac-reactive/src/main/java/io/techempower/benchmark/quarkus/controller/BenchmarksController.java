package io.techempower.benchmark.quarkus.controller;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.techempower.benchmark.quarkus.model.World;
import io.techempower.benchmark.quarkus.util.JteUtils;
import io.techempower.benchmark.quarkus.util.QueryUtils;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Path("/")
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::getMessage);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::getId);

    @GET
    @Path("/plaintext")
    @Produces(MediaType.TEXT_PLAIN)
    public byte[] plaintext() {
        return PLAINTEXT_RESPONSE;
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Message json() {
        return MESSAGE;
    }

    @GET
    @Path("/db")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<World> db() {
        return World.findWorld(QueryUtils.randomWorld());
    }

    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<List<World>> queries(@Nullable @QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<Uni<World>> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            worlds.add(World.findWorld(id));
        }
        return Uni.combine().all().unis(worlds)
                .with(results -> results.stream().map(World.class::cast).toList());
    }

    @GET
    @Path("/updates")
    @Produces(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<List<World>> updates(@Nullable @QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<Uni<World>> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            worlds.add(World.findWorld(id)
                    .map(world -> {
                        world.setRandomNumber(QueryUtils.randomWorld(world.getRandomNumber()));
                        return world;
                    }));
        }

        return Uni.combine().all().unis(worlds)
                .with(results -> results.stream().map(World.class::cast).sorted(WORLD_COMPARATOR).toList())
                .flatMap(updatedWorlds -> {
                    List<Uni<Integer>> updates = updatedWorlds.stream()
                            .map(world -> World.updateRandomNumber(world.getId(), world.getRandomNumber()))
                            .toList();
                    return Uni.combine().all().unis(updates).discardItems().replaceWith(updatedWorlds);
                });
    }

    @GET
    @Path("/fortunes")
    @Produces(MediaType.TEXT_HTML)
    @WithSession
    public Uni<Response> fortunes() {
        return Fortune.findAllFortunes()
                .map(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(FORTUNE_COMPARATOR);
                    return Response.ok(JteUtils.serializeStandard(fortunes))
                            .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                            .build();
                });
    }
    
    public static class Message {
        public String message;
        
        public Message() {}
        
        public Message(String message) {
            this.message = message;
        }
    }
}
