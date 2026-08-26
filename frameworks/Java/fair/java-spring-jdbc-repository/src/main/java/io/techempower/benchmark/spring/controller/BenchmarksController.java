package io.techempower.benchmark.spring.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.Message;
import io.techempower.benchmark.spring.model.World;
import io.techempower.benchmark.spring.repository.FortuneRepository;
import io.techempower.benchmark.spring.repository.WorldRepository;
import io.techempower.benchmark.spring.util.JteUtils;
import io.techempower.benchmark.spring.util.QueryUtils;

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
    public static final MediaType CONTENT_TYPE = MediaType.valueOf("text/html;charset=utf-8");

    private final WorldRepository worldRepository;
    private final FortuneRepository fortuneRepository;

    public BenchmarksController(WorldRepository worldRepository,
                                FortuneRepository fortuneRepository) {
        this.worldRepository = worldRepository;
        this.fortuneRepository = fortuneRepository;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @GetMapping(path = "/plaintext", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> plaintext() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(PLAINTEXT_RESPONSE);
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#json-serialization
    @GetMapping(path = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message json() {
        return MESSAGE;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#single-database-query
    @GetMapping(path = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    public World db() {
        return worldRepository.findById(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @GetMapping(path = "/queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<World> queries(@RequestParam(required = false) String queries) {
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
    @GetMapping(path = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<World> updates(@RequestParam(required = false) String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            World oldWorld = worldRepository.findById(id);
            int newRandomNumber = QueryUtils.randomWorld(oldWorld.randomNumber());
            worldRepository.updateRandomNumber(id, newRandomNumber);
            worlds.add(new World(id, newRandomNumber));
        }

        worlds.sort(WORLD_COMPARATOR);
        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @GetMapping(path = "/fortunes", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> fortunes() {
        List<Fortune> fortunes = new ArrayList<>(fortuneRepository.findAllFortunes());
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));

        fortunes.sort(FORTUNE_COMPARATOR);

        return ResponseEntity.ok()
                .contentType(CONTENT_TYPE)
                .body(JteUtils.serializeStandard(fortunes));
    }
}
