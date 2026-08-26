package io.techempower.benchmark.spring.controller;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.Message;
import io.techempower.benchmark.spring.model.World;
import io.techempower.benchmark.spring.repository.WorldRepository;
import io.techempower.benchmark.spring.util.JteUtils;
import io.techempower.benchmark.spring.util.QueryUtils;
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
import java.util.Comparator;

@RestController
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);
    public static final MediaType CONTENT_TYPE = MediaType.valueOf("text/html;charset=utf-8");

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
    public Mono<ResponseEntity<byte[]>> fortunes() {
        return repository.findAllFortunes()
                .collectList()
                .map(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(FORTUNE_COMPARATOR);
                    return ResponseEntity.ok()
                            .contentType(CONTENT_TYPE)
                            .body(JteUtils.serializeStandard(fortunes));
                });
    }
}
