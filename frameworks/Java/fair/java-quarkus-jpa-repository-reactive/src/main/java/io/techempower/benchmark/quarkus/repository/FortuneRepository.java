package io.techempower.benchmark.quarkus.repository;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FortuneRepository implements PanacheRepositoryBase<Fortune, Integer> {
}
