package io.techempower.benchmark.micronaut.repository;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import io.techempower.benchmark.micronaut.model.Fortune;

@Introspected
@Repository
public interface FortuneRepository extends ReactorCrudRepository<Fortune, Integer> {

}
