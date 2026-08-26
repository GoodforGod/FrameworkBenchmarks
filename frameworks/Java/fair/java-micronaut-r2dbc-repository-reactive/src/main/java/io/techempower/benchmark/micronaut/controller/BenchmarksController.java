package io.techempower.benchmark.micronaut.controller;

import io.micronaut.core.annotation.Introspected;
import io.techempower.benchmark.micronaut.model.Fortune;
import io.techempower.benchmark.micronaut.model.Message;
import io.techempower.benchmark.micronaut.model.World;
import io.techempower.benchmark.micronaut.repository.FortuneRepository;
import io.techempower.benchmark.micronaut.repository.WorldRepository;
import io.techempower.benchmark.micronaut.util.JteUtils;
import io.techempower.benchmark.micronaut.util.QueryUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@Introspected
@Controller
public class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");
    private static final String TEXT_HTML_UTF_8 = "text/html;charset=utf-8";

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);

    private final WorldRepository worldRepository;
    private final FortuneRepository fortuneRepository;

    public BenchmarksController(WorldRepository worldRepository,
                                FortuneRepository fortuneRepository) {
        this.worldRepository = worldRepository;
        this.fortuneRepository = fortuneRepository;
    }

    @Get("/plaintext")
    public HttpResponse<byte[]> plaintext() {
        return HttpResponse.ok(PLAINTEXT_RESPONSE)
                .contentType(MediaType.TEXT_PLAIN_TYPE);
    }

    @Get("/json")
    public Message json() {
        return MESSAGE;
    }

    @Get("/db")
    public Mono<World> db() {
        return worldRepository.findById(QueryUtils.randomWorld());
    }

    @Get("/queries")
    public Mono<List<World>> queries(@Nullable @QueryValue("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Flux.range(0, count)
                .concatMap(i -> worldRepository.findById(QueryUtils.randomWorld()))
                .collectList();
    }

    @Get("/updates")
    public Mono<List<World>> updates(@Nullable @QueryValue("queries") String queries) {
        int count = QueryUtils.parseCount(queries);
        return Flux.range(0, count)
                .concatMap(i -> {
                    int id = QueryUtils.randomWorld();
                    return worldRepository.findRandomNumberById(id)
                            .map(oldRandomNumber -> new World(id, QueryUtils.randomWorld(oldRandomNumber)));
                })
                .collectList()
                .flatMapMany(worlds -> {
                    worlds.sort(WORLD_COMPARATOR);
                    return Flux.fromIterable(worlds)
                            .concatMap(world -> worldRepository.updateRandomNumber(world.id(), world.randomNumber())
                                    .thenReturn(world));
                })
                .collectList();
    }

    @Get(value = "/fortunes", produces = TEXT_HTML_UTF_8)
    public Mono<HttpResponse<byte[]>> fortunes() {
        return fortuneRepository.findAll()
                .collectList()
                .map(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(FORTUNE_COMPARATOR);
                    return HttpResponse.ok(JteUtils.serializeStandard(fortunes))
                            .header("Content-Type", TEXT_HTML_UTF_8);
                });
    }
}
