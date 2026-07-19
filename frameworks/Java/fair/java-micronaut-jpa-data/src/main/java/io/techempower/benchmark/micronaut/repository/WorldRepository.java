package io.techempower.benchmark.micronaut.repository;

import io.techempower.benchmark.micronaut.model.World;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface WorldRepository extends CrudRepository<World, Integer> {

    World findById(int id);

    int findRandomNumberById(int id);

    void updateAll(List<World> worlds);
}
