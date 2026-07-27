package io.techempower.benchmark.micronaut.repository;

import io.micronaut.core.annotation.Introspected;
import io.techempower.benchmark.micronaut.model.World;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

@Introspected
@Repository
public interface WorldRepository extends ReactorCrudRepository<World, Integer> {

    Mono<World> findById(int id);

    Mono<Integer> findRandomNumberById(int id);
}
