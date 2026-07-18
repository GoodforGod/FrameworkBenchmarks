package org.springframework.benchmark.jdbctemplate.controller;

import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import org.springframework.benchmark.jdbctemplate.model.Fortune;
import org.springframework.benchmark.jdbctemplate.model.Message;
import org.springframework.benchmark.jdbctemplate.model.World;
import org.springframework.benchmark.jdbctemplate.repository.WorldRepository;
import org.springframework.benchmark.jdbctemplate.util.QueryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class BenchmarksController {

    private static final Message MESSAGE = new Message("Hello, World!");
    private static final byte[] PLAINTEXT_RESPONSE = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    private static final Comparator<Fortune> FORTUNE_COMPARATOR = Comparator.comparing(Fortune::message);
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::id);

    private final WorldRepository repository;
    private final TemplateEngine templateEngine;

    public BenchmarksController(WorldRepository repository, TemplateEngine templateEngine) {
        this.repository = repository;
        this.templateEngine = templateEngine;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#plaintext
    @GetMapping("/plaintext")
    public ResponseEntity<byte[]> plaintext() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentLength(PLAINTEXT_RESPONSE.length);
        return new ResponseEntity<>(PLAINTEXT_RESPONSE, headers, HttpStatus.OK);
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#json-serialization
    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message json() {
        return MESSAGE;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#single-database-query
    @GetMapping(value = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    public World db() {
        return repository.findById(QueryUtils.randomWorld());
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#multiple-database-queries
    @GetMapping(value = "/queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<World> queries(@RequestParam(required = false) String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            World world = repository.findById(id);
            worlds.add(world);
        }
        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#database-updates
    @GetMapping(value = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<World> updates(@RequestParam(required = false) String queries) {
        int count = QueryUtils.parseCount(queries);
        List<World> worlds = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int id = QueryUtils.randomWorld();
            int oldRandomNumber = repository.findRandomNumberById(id);
            int newRandomNumber = QueryUtils.randomWorld(oldRandomNumber);
            World newWorld = new World(id, newRandomNumber);
            worlds.add(newWorld);
        }

        worlds.sort(WORLD_COMPARATOR);
        
        for (World world : worlds) {
            repository.update(world);
        }
        
        return worlds;
    }

    // https://github.com/TechEmpower/FrameworkBenchmarks/wiki/Project-Information-Framework-Tests-Overview#fortunes
    @GetMapping(value = "/fortunes", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> fortunes() {
        List<Fortune> fortunes = repository.findAllFortunes();
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));
        fortunes.sort(FORTUNE_COMPARATOR);

        TemplateOutput output = new TemplateOutput.ByteBufferOutput(StandardCharsets.UTF_8);
        templateEngine.render("fortunes.jte", java.util.Map.of("fortunes", fortunes), output);
        
        byte[] content = output.toByteBuffer().array();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/html; charset=UTF-8"));
        headers.setContentLength(content.length);
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
