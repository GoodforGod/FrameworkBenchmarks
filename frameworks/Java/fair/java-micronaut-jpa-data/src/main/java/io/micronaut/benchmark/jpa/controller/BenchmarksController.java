package io.micronaut.benchmark.jpa.controller;

import io.micronaut.benchmark.jpa.model.Fortune;
import io.micronaut.benchmark.jpa.model.Message;
import io.micronaut.benchmark.jpa.model.World;
import io.micronaut.benchmark.jpa.repository.FortuneRepository;
import io.micronaut.benchmark.jpa.repository.WorldRepository;
import io.micronaut.benchmark.jpa.util.QueryUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import org.jte.runtime.TemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public final class BenchmarksController {

    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    private static final Message MESSAGE = new Message("Hello, World!");

    private final WorldRepository worldRepository;
    private final FortuneRepository fortuneRepository;
    private final TemplateEngine templateEngine;

    @Inject
    public BenchmarksController(WorldRepository worldRepository, FortuneRepository fortuneRepository, TemplateEngine templateEngine) {
        this.worldRepository = worldRepository;
        this.fortuneRepository = fortuneRepository;
        this.templateEngine = templateEngine;
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
    public World db() {
        return worldRepository.findById(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @Get(value = "/queries", produces = MediaType.APPLICATION_JSON)
    public List<World> queries(@QueryValue(value = "queries", defaultValue = "1") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            World world = worldRepository.findById(id);
            worlds.add(world);
        }

        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @Get(value = "/updates", produces = MediaType.APPLICATION_JSON)
    public List<World> updates(@QueryValue(value = "queries", defaultValue = "1") String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            World oldWorld = worldRepository.findById(id);
            int newRandomNumber = QueryUtils.randomWorld(oldWorld.getRandomNumber());
            oldWorld.setRandomNumber(newRandomNumber);
            worlds.add(oldWorld);
        }

        Collections.sort(worlds, (w1, w2) -> Integer.compare(w1.getId(), w2.getId()));
        worldRepository.updateAll(worlds);
        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @Get(value = "/fortunes", produces = MediaType.TEXT_HTML)
    public String fortunes() {
        List<Fortune> fortunes = new ArrayList<>(fortuneRepository.findAll());
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));

        Collections.sort(fortunes);
        
        return templateEngine.render(new FortunesModel(fortunes));
    }
}
