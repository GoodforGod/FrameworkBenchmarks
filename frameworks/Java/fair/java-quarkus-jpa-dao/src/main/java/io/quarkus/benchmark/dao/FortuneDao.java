package io.quarkus.benchmark.dao;

import io.quarkus.benchmark.entity.Fortune;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FortuneDao implements PanacheRepository<Fortune> {
    
    public List<Fortune> findAllFortunes() {
        return listAll();
    }
}
