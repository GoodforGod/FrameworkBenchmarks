package io.techempower.benchmark.helidon.repository;

import io.techempower.benchmark.helidon.model.World;
import io.helidon.data.Data;

@Data.Repository
public interface WorldDataRepository extends Data.CrudRepository<World, Integer> {
}
