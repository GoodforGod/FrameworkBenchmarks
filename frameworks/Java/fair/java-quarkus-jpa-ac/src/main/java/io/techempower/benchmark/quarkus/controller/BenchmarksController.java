package io.techempower.benchmark.quarkus.controller;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.techempower.benchmark.quarkus.model.World;
import io.techempower.benchmark.quarkus.util.JteUtils;
import io.techempower.benchmark.quarkus.util.QueryUtils;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;
import io.smallrye.common.annotation.RunOnVirtualThread;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Path("/")
@RunOnVirtualThread
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
    public World db() {
        return World.findWorld(QueryUtils.randomWorld());
    }

    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> queries(@Nullable @QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            var world = World.findWorld(id);
            worlds.add(world);
        }
        return worlds;
    }

    @GET
    @Path("/updates")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public List<World> updates(@Nullable @QueryParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            World world = World.findWorld(QueryUtils.randomWorld());
            // Keep this endpoint in the Panache active-record style: load a managed entity,
            // read the current value required by TechEmpower, mutate it, and let Hibernate
            // flush dirty entities inside the Quarkus transaction.
            world.setRandomNumber(QueryUtils.randomWorld(world.getRandomNumber()));
            worlds.add(world);
        }

        worlds.sort(WORLD_COMPARATOR);
        return worlds;
    }

    @GET
    @Path("/fortunes")
    @Produces(MediaType.TEXT_HTML)
    public Response fortunes() {
        List<Fortune> fortunes = Fortune.findAllFortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));
        fortunes.sort(FORTUNE_COMPARATOR);

        return Response.ok(JteUtils.serializeStandard(fortunes))
                .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                .build();
    }

    public static class Message {
        public String message;

        public Message() {
        }

        public Message(String message) {
            this.message = message;
        }
    }
}
