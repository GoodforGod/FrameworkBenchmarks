package io.spring.benchmark.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class BenchmarksControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testPlaintext() {
        webTestClient.get().uri("/plaintext")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, World!");
    }

    @Test
    void testJson() {
        webTestClient.get().uri("/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Hello, World!");
    }

    @Test
    void testDb() {
        webTestClient.get().uri("/db")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.randomNumber").exists();
    }

    @Test
    void testQueries() {
        webTestClient.get().uri("/queries?queries=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    void testUpdates() {
        webTestClient.get().uri("/updates?queries=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    void testFortunes() {
        webTestClient.get().uri("/fortunes")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<table>");
                    assert html.contains("Additional fortune added at request time.");
                });
    }
}
