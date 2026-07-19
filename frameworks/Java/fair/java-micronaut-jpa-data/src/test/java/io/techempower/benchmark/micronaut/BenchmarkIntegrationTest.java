package io.techempower.benchmark.micronaut;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

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
        postgres = new PostgreSQLContainer<>("postgres:18.4-alpine")
                .withNetwork(NETWORK).withNetworkAliases("postgres")
                .withDatabaseName("postgres").withUsername("postgres").withPassword("postgres")
                .withStartupTimeout(Duration.ofSeconds(30))
                .waitingFor(Wait.forLogMessage(".*ready.*", 2));

        app = new GenericContainer<>(new ImageFromDockerfile("fair-micronaut-jpa-data").withDockerfile(Paths.get("Dockerfile").toAbsolutePath()))
                .withNetwork(NETWORK).withExposedPorts(8080)
                .withStartupTimeout(Duration.ofSeconds(30))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(BenchmarkIntegrationTest.class)))
                .withEnv("POSTGRES_JDBC_URL", "jdbc:postgresql://postgres:5432/postgres")
                .withEnv("POSTGRES_USER", "postgres").withEnv("POSTGRES_PASS", "postgres")
                .dependsOn(postgres)
                .waitingFor(Wait.forHttp("/plaintext").forPort(8080).forStatusCode(200));

        postgres.start(); 
        app.start();
        baseUrl = "http://" + app.getHost() + ":" + app.getMappedPort(8080);
        httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Test void testPlaintext() throws Exception { var r = sendGet("/plaintext"); assertEquals(200, r.statusCode()); }
    @Test void testJson() throws Exception { var r = sendGet("/json"); assertEquals(200, r.statusCode()); }
    @Test void testDb() throws Exception { var r = sendGet("/db"); assertEquals(200, r.statusCode()); }
    @Test void testQueries() throws Exception { var r = sendGet("/queries?queries=5"); assertEquals(200, r.statusCode()); }
    @Test void testUpdates() throws Exception { var r = sendGet("/updates?queries=5"); assertEquals(200, r.statusCode()); }
    @Test void testFortunes() throws Exception { var r = sendGet("/fortunes"); assertEquals(200, r.statusCode()); }

    private HttpResponse<String> sendGet(String path) throws Exception {
        return httpClient.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}
