package io.techempower.benchmark.micronaut.controller;

import io.micronaut.core.annotation.Introspected;
import io.techempower.benchmark.micronaut.model.Fortune;
import io.techempower.benchmark.micronaut.model.Message;
import io.techempower.benchmark.micronaut.model.World;
import io.techempower.benchmark.micronaut.repository.FortuneRepository;
import io.techempower.benchmark.micronaut.repository.WorldRepository;
import io.techempower.benchmark.micronaut.util.JteUtils;
import io.techempower.benchmark.micronaut.util.QueryUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Introspected
@Controller
public final class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");
    private static final String TEXT_HTML_UTF_8 = "text/html;charset=utf-8";

    private final WorldRepository worldRepository;
    private final FortuneRepository fortuneRepository;

    @Inject
    public BenchmarksController(WorldRepository worldRepository, FortuneRepository fortuneRepository) {
        this.worldRepository = worldRepository;
        this.fortuneRepository = fortuneRepository;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @Get("/plaintext")
    public HttpResponse<byte[]> plaintext() {
        return HttpResponse.ok(PLAINTEXT_RESPONSE)
                .contentType(MediaType.TEXT_PLAIN_TYPE)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(PLAINTEXT_RESPONSE.length));
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#json-serialization
    @Get(value = "/json", produces = MediaType.APPLICATION_JSON)
    public Message json() {
        return MESSAGE;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#single-database-query
    @Get(value = "/db", produces = MediaType.APPLICATION_JSON)
    public Mono<World> db() {
        return worldRepository.findById(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @Get(value = "/queries", produces = MediaType.APPLICATION_JSON)
    public Mono<List<World>> queries(@QueryValue(value = "queries", defaultValue = "1") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Flux.range(0, count)
                .flatMap(i -> worldRepository.findById(QueryUtils.randomWorld()))
                .collectList();
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @Get(value = "/updates", produces = MediaType.APPLICATION_JSON)
    public Mono<List<World>> updates(@QueryValue(value = "queries", defaultValue = "1") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Flux.range(0, count)
                .flatMap(i -> {
                    int id = QueryUtils.randomWorld();
                    return worldRepository.findById(id)
                            .map(world -> {
                                world.setRandomNumber(QueryUtils.randomWorld(world.getRandomNumber()));
                                return world;
                            });
                })
                .sort((w1, w2) -> Integer.compare(w1.getId(), w2.getId()))
                .flatMap(world -> worldRepository.update(world))
                .collectList();
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @Get(value = "/fortunes", produces = TEXT_HTML_UTF_8)
    public Mono<HttpResponse<byte[]>> fortunes() {
        return fortuneRepository.findAll()
                .collectList()
                .map(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    Collections.sort(fortunes);
                    return HttpResponse.ok(JteUtils.serializeStandard(fortunes))
                            .header(HttpHeaders.CONTENT_TYPE, TEXT_HTML_UTF_8);
                });
    }
}
