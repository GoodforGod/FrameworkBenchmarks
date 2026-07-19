package io.techempower.benchmark.helidon.controller;

import io.techempower.benchmark.helidon.model.Fortune;
import io.techempower.benchmark.helidon.model.World;
import io.techempower.benchmark.helidon.repository.FortuneDataRepository;
import io.techempower.benchmark.helidon.repository.WorldDataRepository;
import io.techempower.benchmark.helidon.util.JteUtils;
import io.helidon.config.Config;
import io.helidon.service.registry.Services;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.techempower.benchmark.helidon.util.QueryUtils.parseCount;
import static io.techempower.benchmark.helidon.util.QueryUtils.randomWorld;

@Path("/")
@ApplicationScoped
public class BenchmarksController {
    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");
    private static final Fortune ADDITIONAL_FORTUNE = new Fortune(0, "Additional fortune added at request time.");
    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::getMessage);

    private final WorldDataRepository worlds;
    private final FortuneDataRepository fortunes;

    @Inject
    public BenchmarksController(Config config) {
        Services.set(Config.class, config);
        this.worlds = Services.get(WorldDataRepository.class);
        this.fortunes = Services.get(FortuneDataRepository.class);
    }

    @GET
    @Path("/plaintext")
    @Produces(MediaType.TEXT_PLAIN)
    public Response plaintext() {
        return Response.ok(PLAINTEXT_RESPONSE)
                .header(HttpHeaders.CONTENT_LENGTH, PLAINTEXT_RESPONSE.length)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
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
        return getWorld(randomWorld());
    }

    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> queries(@QueryParam("queries") String queries) {
        int count = parseCount(queries);
        List<World> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(getWorld(randomWorld()));
        }
        return result;
    }

    @GET
    @Path("/updates")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> updates(@QueryParam("queries") String queries) {
        int count = parseCount(queries);
        List<World> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            World world = getWorld(randomWorld());
            world.randomNumber = randomWorld(world.id);
            result.add(world);
        }
        worlds.updateAll(result);
        return result;
    }

    @GET
    @Path("/fortunes")
    @Produces(MediaType.TEXT_HTML)
    public Response fortunes() {
        List<Fortune> result;
        try (var stream = fortunes.findAll()) {
            result = new ArrayList<>(stream.toList());
        }
        result.add(ADDITIONAL_FORTUNE);
        result.sort(FORTUNE_COMPARATOR);

        return Response.ok(JteUtils.serializeStandard(result))
                .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                .build();
    }

    private World getWorld(int id) {
        return worlds.findById(id).orElseThrow();
    }

    public record Message(String message) {
    }
}
