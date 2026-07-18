package io.spring.benchmark.controller;

import io.spring.benchmark.entity.Fortune;
import io.spring.benchmark.entity.Message;
import io.spring.benchmark.entity.World;
import io.spring.benchmark.repository.WorldRepository;
import io.spring.benchmark.util.QueryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::getMessage);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::getId);

    private final WorldRepository repository;

    public BenchmarksController(WorldRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/plaintext")
    public ResponseEntity<byte[]> plaintext() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(PLAINTEXT_RESPONSE);
    }

    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message json() {
        return MESSAGE;
    }

    @GetMapping(value = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    public World db() {
        return repository.findById(QueryUtils.randomWorld()).orElse(null);
    }

    @GetMapping(value = "/queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<World> queries(@Nullable @RequestParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            repository.findById(id).ifPresent(worlds::add);
        }
        return worlds;
    }

    @GetMapping(value = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<World> updates(@Nullable @RequestParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            var existingWorld = repository.findById(id);
            if (existingWorld.isPresent()) {
                var world = existingWorld.get();
                world.setRandomNumber(QueryUtils.randomWorld(world.getRandomNumber()));
                worlds.add(world);
            }
        }

        worlds.sort(WORLD_COMPARATOR);
        repository.saveAll(worlds);
        return worlds;
    }

    @GetMapping(value = "/fortunes", produces = MediaType.TEXT_HTML_VALUE)
    public String fortunes() {
        List<Fortune> fortunes = repository.findAllFortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));
        fortunes.sort(FORTUNE_COMPARATOR);
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>Fortunes</title></head><body><table>");
        html.append("<tr><th>id</th><th>message</th></tr>");
        for (Fortune fortune : fortunes) {
            html.append("<tr><td>").append(fortune.getId()).append("</td><td>");
            html.append(escapeHtml(fortune.getMessage())).append("</td></tr>");
        }
        html.append("</table></body></html>");
        return html.toString();
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
