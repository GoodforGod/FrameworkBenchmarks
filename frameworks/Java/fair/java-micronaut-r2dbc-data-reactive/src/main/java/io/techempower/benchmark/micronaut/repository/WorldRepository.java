package io.techempower.benchmark.micronaut.repository;

import io.micronaut.core.annotation.Introspected;
import io.techempower.benchmark.micronaut.model.World;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

@Introspected
@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface WorldRepository extends ReactorCrudRepository<World, Integer> {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    Mono<World> findById(int id);

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    Mono<Integer> findRandomNumberById(int id);

    @Query("UPDATE world SET randomnumber = :randomNumber WHERE id = :id")
    Mono<Integer> updateRandomNumber(int id, int randomNumber);
}
