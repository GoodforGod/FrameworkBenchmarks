package io.spring.benchmark.controller;

import io.spring.benchmark.model.Fortune;
import io.spring.benchmark.model.Message;
import io.spring.benchmark.model.World;
import io.spring.benchmark.repository.WorldRepository;
import io.spring.benchmark.util.QueryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);

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
    public Mono<Message> json() {
        return Mono.just(MESSAGE);
    }

    @GetMapping(value = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<World> db() {
        return repository.findById(QueryUtils.randomWorld());
    }

    @GetMapping(value = "/queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<World> queries(@Nullable @RequestParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Flux.range(0, count)
                .concatMap(i -> repository.findById(QueryUtils.randomWorld()));
    }

    @GetMapping(value = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<World> updates(@Nullable @RequestParam("queries") String queries) {
        int count = QueryUtils.parseCount(queries);

        return Flux.range(0, count)
                .concatMap(i -> {
                    int id = QueryUtils.randomWorld();
                    return repository.findRandomNumberById(id)
                            .map(oldRandomNumber -> new World(id, QueryUtils.randomWorld(oldRandomNumber)));
                })
                .collectList()
                .flatMapMany(worlds -> {
                    worlds.sort(WORLD_COMPARATOR);
                    return Flux.fromIterable(worlds)
                            .concatMap(world -> repository.updateRandomNumber(world.id(), world.randomNumber())
                                    .thenReturn(world));
                });
    }

    @GetMapping(value = "/fortunes", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> fortunes() {
        return repository.findAllFortunes()
                .collectList()
                .map(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(FORTUNE_COMPARATOR);
                    return renderFortunes(fortunes);
                });
    }

    private String renderFortunes(List<Fortune> fortunes) {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("<!DOCTYPE html><html><head><title>Fortunes</title></head><body><table>");
        sb.append("<tr><th>id</th><th>message</th></tr>");
        for (Fortune fortune : fortunes) {
            sb.append("<tr><td>").append(fortune.id()).append("</td><td>");
            sb.append(escapeHtml(fortune.message())).append("</td></tr>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
