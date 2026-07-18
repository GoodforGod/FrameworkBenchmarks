package io.quarkus.benchmark.controller;

import io.quarkus.benchmark.model.Fortune;
import io.quarkus.benchmark.model.Message;
import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.util.QueryUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
        return World.findById(QueryUtils.randomWorld());
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
            var world = World.findById(id);
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
            var world = World.findById(id);
            world.randomNumber = QueryUtils.randomWorld();
            world.persist();
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
        List<Fortune> fortunes = Fortune.listAll();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));

        fortunes.sort(FORTUNE_COMPARATOR);

        String html = renderFortunes(fortunes);
        return Response.ok(html)
                .type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8"))
                .build();
    }

    private String renderFortunes(List<Fortune> fortunes) {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("<!DOCTYPE html><html><head><title>Fortunes</title></head><body><table><tr><th>id</th><th>message</th></tr>");
        for (Fortune fortune : fortunes) {
            sb.append("<tr><td>").append(fortune.getIdInt()).append("</td><td>");
            escapeHtml(fortune.message, sb);
            sb.append("</td></tr>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    private void escapeHtml(String input, StringBuilder sb) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
            }
        }
    }
}
