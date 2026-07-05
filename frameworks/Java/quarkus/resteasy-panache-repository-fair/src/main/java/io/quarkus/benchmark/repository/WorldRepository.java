package io.quarkus.benchmark.repository;

import io.quarkus.benchmark.model.World;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorldRepository implements PanacheRepositoryBase<World, Integer> {

}