package io.koraframework.benchmark.repository;

import io.koraframework.benchmark.model.World;
import io.koraframework.database.common.annotation.Batch;
import io.koraframework.database.common.annotation.Query;
import io.koraframework.database.common.annotation.Repository;
import io.koraframework.database.jdbc.JdbcRepository;

import java.util.List;

@Repository
public interface WorldRepository extends JdbcRepository {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    World findById(int id);

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    int findRandomNumberById(int id);

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    void update(@Batch List<World> world);
}
