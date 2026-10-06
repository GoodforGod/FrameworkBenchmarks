package io.techempower.benchmark.spring.repository;

import java.util.List;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.World;
import org.springframework.r2dbc.core.DatabaseClient;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
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

    public Mono<Void> updateRandomNumbers(List<World> worlds) {
        return databaseClient.inConnectionMany(connection -> {
            Statement statement = connection.createStatement("UPDATE world SET randomnumber = $1 WHERE id = $2");
            for (int i = 0; i < worlds.size(); i++) {
                World world = worlds.get(i);
                if (i > 0) {
                    statement.add();
                }
                statement.bind(0, world.randomNumber()).bind(1, world.id());
            }
            return Flux.from(statement.execute()).flatMap(Result::getRowsUpdated);
        }).then();
    }

    public Flux<Fortune> findAllFortunes() {
        return databaseClient.sql("SELECT id, message FROM fortune")
                .map((row, metadata) -> new Fortune(row.get("id", Integer.class), row.get("message", String.class)))
                .all();
    }
}
