package io.techempower.benchmark.quarkus.repository;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FortuneRepository implements PanacheRepository<Fortune> {

}
