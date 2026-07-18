package org.springframework.benchmark.jdbc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests using Testcontainers.
 * Runs the application in Docker and tests all endpoints.
 */
@Testcontainers
class BenchmarkIntegrationTest {

    private static final Network NETWORK = Network.newNetwork();
    private static PostgreSQLContainer<?> postgres;
    private static GenericContainer<?> app;
    private static HttpClient httpClient;
    private static String baseUrl;

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    @BeforeAll
    static void setup() {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withNetwork(NETWORK)
                .withNetworkAliases("postgres")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres")
                .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2));

        app = new GenericContainer<>(DockerImageName.parse("fair-spring-jdbc-data:latest"))
                .withNetwork(NETWORK)
                .withExposedPorts(8080)
                .withEnv("POSTGRES_JDBC_URL", "jdbc:postgresql://postgres:5432/postgres")
                .withEnv("POSTGRES_USER", "postgres")
                .withEnv("POSTGRES_PASS", "postgres")
                .dependsOn(postgres)
                .waitingFor(Wait.forHttp("/plaintext").forPort(8080).forStatusCode(200));

        postgres.start();
        app.start();

        baseUrl = "http://" + app.getHost() + ":" + app.getMappedPort(8080);
        httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Test
    void testPlaintext() throws Exception {
        HttpResponse<String> response = sendGet("/plaintext");
        assertEquals(200, response.statusCode());
        assertEquals("Hello, World!", response.body());
    }

    @Test
    void testJson() throws Exception {
        HttpResponse<String> response = sendGet("/json");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\""));
    }

    @Test
    void testDb() throws Exception {
        HttpResponse<String> response = sendGet("/db");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\""));
    }

    @Test
    void testQueries() throws Exception {
        HttpResponse<String> response = sendGet("/queries?queries=5");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().startsWith("["));
    }

    @Test
    void testUpdates() throws Exception {
        HttpResponse<String> response = sendGet("/updates?queries=5");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().startsWith("["));
    }

    @Test
    void testFortunes() throws Exception {
        HttpResponse<String> response = sendGet("/fortunes");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("<table>"));
    }

    private HttpResponse<String> sendGet(String path) throws Exception {
        return httpClient.send(
            HttpRequest.newBuilder().uri(URI.create(baseUrl + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );
    }
}
