package io.techempower.benchmark.micronaut.repository;

import io.micronaut.core.annotation.Introspected;
import io.techempower.benchmark.micronaut.model.Fortune;
import io.techempower.benchmark.micronaut.model.World;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Introspected
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WorldRepository extends CrudRepository<World, Integer> {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    World findById(int id);

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    int findRandomNumberById(int id);

    @Query("UPDATE world SET randomnumber = :randomNumber WHERE id = :id")
    void updateRandomNumber(int id, int randomNumber);

    @Query("SELECT id, message FROM fortune")
    List<Fortune> fortunes();
}
