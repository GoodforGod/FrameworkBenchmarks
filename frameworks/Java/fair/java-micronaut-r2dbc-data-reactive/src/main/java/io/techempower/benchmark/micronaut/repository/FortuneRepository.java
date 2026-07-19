package io.techempower.benchmark.micronaut.repository;

import io.techempower.benchmark.micronaut.model.Fortune;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface FortuneRepository extends ReactorCrudRepository<Fortune, Integer> {

    @Query("SELECT id, message FROM fortune")
    Flux<Fortune> findAll();
}
