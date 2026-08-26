package io.techempower.benchmark.micronaut.repository;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import io.techempower.benchmark.micronaut.model.Fortune;

@Introspected
@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface FortuneRepository extends ReactorCrudRepository<Fortune, Integer> {

}
