package io.techempower.benchmark.spring.repository;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.World;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WorldRepository extends ReactiveCrudRepository<World, Integer> {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    Mono<World> findById(@Param("id") int id);

    @Query("SELECT randomnumber FROM world WHERE id = :id")
    Mono<Integer> findRandomNumberById(@Param("id") int id);

    @Query("UPDATE world SET randomnumber = :randomNumber WHERE id = :id")
    Mono<Void> updateRandomNumber(@Param("id") int id, @Param("randomNumber") int randomNumber);

    @Query("SELECT id, message FROM fortune")
    Flux<Fortune> findAllFortunes();
}
