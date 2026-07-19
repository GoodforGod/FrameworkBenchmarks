
package io.techempower.benchmark.helidon.repository;

import io.techempower.benchmark.helidon.model.Fortune;
import io.techempower.benchmark.helidon.model.World;

import java.util.List;

public interface DbRepository {

    World getWorld(int id);

    List<World> getWorlds(int count);

    List<World> updateWorlds(int count);

    List<Fortune> getFortunes();
}
