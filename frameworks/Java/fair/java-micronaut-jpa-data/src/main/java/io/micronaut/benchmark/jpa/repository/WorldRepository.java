package io.micronaut.benchmark.jpa.repository;

import io.micronaut.benchmark.jpa.model.World;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WorldRepository extends CrudRepository<World, Integer> {

    World findById(int id);

    int findRandomNumberById(int id);

    void updateAll(List<World> worlds);
}
