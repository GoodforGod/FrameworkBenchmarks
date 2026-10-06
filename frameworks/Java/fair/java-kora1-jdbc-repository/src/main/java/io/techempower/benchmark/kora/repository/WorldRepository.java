package io.techempower.benchmark.kora.repository;

import io.techempower.benchmark.kora.model.Fortune;
import io.techempower.benchmark.kora.model.World;
import ru.tinkoff.kora.database.common.annotation.Batch;
import ru.tinkoff.kora.database.common.annotation.Query;
import ru.tinkoff.kora.database.common.annotation.Repository;
import ru.tinkoff.kora.database.jdbc.JdbcRepository;

import java.util.List;

@Repository
public interface WorldRepository extends JdbcRepository {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    World findById(int id);

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    int findRandomNumberById(int id);

    @Query("UPDATE world SET randomnumber = :world.randomNumber WHERE id = :world.id")
    void update(@Batch List<World> world);

    @Query("SELECT id, message FROM fortune")
    List<Fortune> fortunes();
}
