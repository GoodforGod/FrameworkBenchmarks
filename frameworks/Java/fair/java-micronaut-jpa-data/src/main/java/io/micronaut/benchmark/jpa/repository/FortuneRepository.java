package io.micronaut.benchmark.jpa.repository;

import io.micronaut.benchmark.jpa.model.Fortune;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FortuneRepository extends CrudRepository<Fortune, Integer> {

    List<Fortune> findAll();
}
