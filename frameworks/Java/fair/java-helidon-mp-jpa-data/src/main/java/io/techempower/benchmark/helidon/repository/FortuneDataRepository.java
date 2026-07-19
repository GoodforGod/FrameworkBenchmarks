package io.techempower.benchmark.helidon.repository;

import io.techempower.benchmark.helidon.model.Fortune;
import io.helidon.data.Data;

@Data.Repository
public interface FortuneDataRepository extends Data.BasicRepository<Fortune, Integer> {
}
