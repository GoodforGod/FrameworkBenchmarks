package io.quarkus.benchmark.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BenchmarksControllerTest {

    @Test
    void testPlaintext() {
        given()
            .when().get("/plaintext")
            .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(equalTo("Hello, World!"));
    }

    @Test
    void testJson() {
        given()
            .when().get("/json")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Hello, World!"));
    }

    @Test
    void testDb() {
        given()
            .when().get("/db")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("randomNumber", notNullValue());
    }

    @Test
    void testQueries() {
        given()
            .when().get("/queries?queries=5")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(5))
            .body("[0].id", notNullValue())
            .body("[0].randomNumber", notNullValue());
    }

    @Test
    void testUpdates() {
        given()
            .when().get("/updates?queries=5")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(5))
            .body("[0].id", notNullValue())
            .body("[0].randomNumber", notNullValue());
    }

    @Test
    void testFortunes() {
        given()
            .when().get("/fortunes")
            .then()
            .statusCode(200)
            .contentType(ContentType.HTML)
            .body(containsString("<table>"))
            .body(containsString("Additional fortune added at request time."));
    }
}
