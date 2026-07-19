package io.techempower.benchmark.micronaut.repository;

import io.techempower.benchmark.micronaut.model.Fortune;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface FortuneRepository extends CrudRepository<Fortune, Integer> {

    List<Fortune> findAll();
}
