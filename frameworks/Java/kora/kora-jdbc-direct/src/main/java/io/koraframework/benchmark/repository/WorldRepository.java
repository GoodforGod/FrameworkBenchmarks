package io.koraframework.benchmark.repository;

import io.koraframework.benchmark.model.Fortune;
import io.koraframework.benchmark.model.World;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import ru.tinkoff.kora.database.common.UpdateCount;
import ru.tinkoff.kora.database.common.annotation.Batch;
import ru.tinkoff.kora.database.common.annotation.Query;
import ru.tinkoff.kora.database.common.annotation.Repository;
import ru.tinkoff.kora.database.jdbc.JdbcRepository;

@Repository
public interface WorldRepository extends JdbcRepository {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    World findById(int id);

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    void update(@Batch List<World> world);

    @Query("SELECT id, message FROM fortune")
    List<Fortune> fortunes();
}
