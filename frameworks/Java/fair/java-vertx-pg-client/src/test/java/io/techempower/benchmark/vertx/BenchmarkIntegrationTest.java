package io.techempower.benchmark.vertx;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class BenchmarkIntegrationTest {

    private static final Network NETWORK = Network.newNetwork();
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private static PostgreSQLContainer<?> postgres;
    private static GenericContainer<?> app;
    private static HttpClient httpClient;
    private static String baseUrl;

    @BeforeAll
    static void setup() throws Exception {
        postgres = new PostgreSQLContainer<>("postgres:18.4-alpine")
                .withNetwork(NETWORK)
                .withNetworkAliases("postgres")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres")
                .withStartupTimeout(Duration.ofSeconds(30))
                .waitingFor(Wait.forLogMessage(".*ready.*", 2));

        app = new GenericContainer<>(new ImageFromDockerfile("fair-vertx-pg-client").withDockerfile(Paths.get("Dockerfile").toAbsolutePath()))
                .withNetwork(NETWORK)
                .withExposedPorts(8080)
                .withStartupTimeout(Duration.ofSeconds(30))
                .withLogConsumer(frame -> System.err.print(frame.getUtf8String()))
                .withEnv("POSTGRES_REACTIVE_URL", "postgresql://postgres:5432/postgres")
                .withEnv("POSTGRES_USER", "postgres")
                .withEnv("POSTGRES_PASS", "postgres")
                .dependsOn(postgres)
                .waitingFor(Wait.forHttp("/plaintext").forPort(8080).forStatusCode(200));

        postgres.start();
        initDatabase();
        app.start();
        baseUrl = "http://" + app.getHost() + ":" + app.getMappedPort(8080);
        httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Test
    void testPlaintext() throws Exception {
        var response = sendGet("/plaintext");
        assertEquals(200, response.statusCode());
    }

    @Test
    void testJson() throws Exception {
        var response = sendGet("/json");
        assertEquals(200, response.statusCode());
    }

    @Test
    void testDb() throws Exception {
        var response = sendGet("/db");
        assertEquals(200, response.statusCode());
    }

    @Test
    void testQueries() throws Exception {
        var response = sendGet("/queries?queries=5");
        assertEquals(200, response.statusCode());
    }

    @Test
    void testUpdates() throws Exception {
        var response = sendGet("/updates?queries=5");
        assertEquals(200, response.statusCode());
    }

    @Test
    void testFortunes() throws Exception {
        var response = sendGet("/fortunes");
        assertEquals(200, response.statusCode());
    }

    private static void initDatabase() throws Exception {
        try (var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS world (
                        id integer NOT NULL PRIMARY KEY,
                        randomnumber integer NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS fortune (
                        id integer NOT NULL PRIMARY KEY,
                        message varchar(2048) NOT NULL
                    )
                    """);
            statement.execute("TRUNCATE TABLE world, fortune");
            statement.execute("INSERT INTO world (id, randomnumber) SELECT i, i FROM generate_series(1, 10000) AS i");
            statement.execute("""
                    INSERT INTO fortune (id, message) VALUES
                    (1, 'fortune: No such file or directory'),
                    (2, 'A computer scientist is someone who fixes things that are not broken.'),
                    (3, 'After enough decimal places, nobody cares.'),
                    (4, 'A bad random number generator: 1, 1, 1, 1, 1'),
                    (5, 'A computer program does what you tell it to do, not what you want it to do.'),
                    (6, 'Emacs is a nice operating system, but I prefer UNIX.'),
                    (7, 'Any program that runs right is obsolete.'),
                    (8, 'A list is only as strong as its weakest link.'),
                    (9, 'Feature: A bug with seniority.'),
                    (10, 'Computers make very fast, very accurate mistakes.'),
                    (11, '<script>alert(""This should not be displayed in a browser alert box."" );</script>'),
                    (12, 'Framework benchmarks')
                    """);
        }
    }

    private HttpResponse<String> sendGet(String path) throws Exception {
        return httpClient.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}
