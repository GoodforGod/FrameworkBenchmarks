package io.techempower.benchmark.helidon.controller;

import io.techempower.benchmark.helidon.repository.DbRepository;
import io.techempower.benchmark.helidon.model.Fortune;
import io.techempower.benchmark.helidon.model.World;
import io.techempower.benchmark.helidon.util.JteUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import static io.techempower.benchmark.helidon.util.QueryUtils.parseCount;
import static io.techempower.benchmark.helidon.util.QueryUtils.randomWorld;

@Path("/")
@RequestScoped
public class BenchmarksController {
    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");
    private static final Fortune ADDITIONAL_FORTUNE = new Fortune(0, "Additional fortune added at request time.");
    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::getMessage);

    private final DbRepository repository;

    @Inject
    public BenchmarksController(DbRepository repository) {
        this.repository = repository;
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
        return repository.getWorld(randomWorld());
    }

    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> queries(@QueryParam("queries") String queries) {
        return repository.getWorlds(parseCount(queries));
    }

    @GET
    @Path("/updates")
    @Produces(MediaType.APPLICATION_JSON)
    public List<World> updates(@QueryParam("queries") String queries) {
        return repository.updateWorlds(parseCount(queries));
    }

    @GET
    @Path("/fortunes")
    @Produces(MediaType.TEXT_HTML)
    public Response fortunes() {
        List<Fortune> fortunes = repository.getFortunes();
        fortunes.add(ADDITIONAL_FORTUNE);
        fortunes.sort(FORTUNE_COMPARATOR);

        return Response.ok(JteUtils.serializeStandard(fortunes))
                .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                .build();
    }

    public record Message(String message) {
    }
}
