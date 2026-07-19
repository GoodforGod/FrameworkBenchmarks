package io.techempower.benchmark.quarkus.repository;

import io.techempower.benchmark.quarkus.model.World;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorldRepository implements PanacheRepository<World> {

    public World findWorld(int id) {
        return findById((long) id);
    }
}
