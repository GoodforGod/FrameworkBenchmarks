package io.techempower.benchmark.micronaut.repository;

import io.techempower.benchmark.micronaut.model.Fortune;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;

@Repository
public interface FortuneRepository extends ReactorCrudRepository<Fortune, Integer> {

    Flux<Fortune> findAll();
}
