package io.techempower.benchmark.spring.repository;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.World;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class WorldRepository {

    private final DatabaseClient databaseClient;

    public WorldRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<World> findById(int id) {
        return databaseClient.sql("SELECT id, randomnumber FROM world WHERE id = :id")
                .bind("id", id)
                .map((row, metadata) -> new World(row.get("id", Integer.class), row.get("randomnumber", Integer.class)))
                .one();
    }

    public Mono<Integer> findRandomNumberById(int id) {
        return databaseClient.sql("SELECT randomnumber FROM world WHERE id = :id")
                .bind("id", id)
                .map((row, metadata) -> row.get("randomnumber", Integer.class))
                .one();
    }

    public Mono<Void> updateRandomNumber(int id, int randomNumber) {
        return databaseClient.sql("UPDATE world SET randomnumber = :randomNumber WHERE id = :id")
                .bind("id", id)
                .bind("randomNumber", randomNumber)
                .fetch()
                .rowsUpdated()
                .then();
    }

    public Flux<Fortune> findAllFortunes() {
        return databaseClient.sql("SELECT id, message FROM fortune")
                .map((row, metadata) -> new Fortune(row.get("id", Integer.class), row.get("message", String.class)))
                .all();
    }
}
