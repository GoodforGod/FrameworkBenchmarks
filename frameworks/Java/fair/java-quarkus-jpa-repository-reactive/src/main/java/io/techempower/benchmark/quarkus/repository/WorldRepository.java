package io.techempower.benchmark.quarkus.repository;

import io.techempower.benchmark.quarkus.model.World;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorldRepository implements PanacheRepositoryBase<World, Integer> {

    public Uni<World> findWorld(int id) {
        return findById(id);
    }
}
