package io.techempower.benchmark.helidon.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.techempower.benchmark.helidon.model.Fortune;
import io.techempower.benchmark.helidon.model.World;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;

import static io.techempower.benchmark.helidon.util.QueryUtils.randomWorld;

public final class HelidonDbClientRepository implements DbRepository {
    private static final String SELECT_WORLD = "SELECT id, randomnumber FROM world WHERE id = ?";
    private static final String SELECT_FORTUNES = "SELECT id, message FROM fortune";
    private static final String UPDATE_WORLD = "UPDATE world SET randomnumber = ? WHERE id = ?";
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(world -> world.id);

    private final DbClient dbClient;

    public HelidonDbClientRepository(Config config) {
        this.dbClient = DbClient.builder("jdbc").config(config).build();
    }

    @Override
    public World getWorld(int id) {
        return dbClient.execute()
                .createGet(SELECT_WORLD)
                .addParam(id)
                .execute()
                .map(HelidonDbClientRepository::mapWorld)
                .orElseThrow();
    }

    @Override
    public List<World> getWorlds(int count) {
        List<World> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(getWorld(randomWorld()));
        }
        return result;
    }

    @Override
    public List<World> updateWorlds(int count) {
        List<World> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            World world = getWorld(randomWorld());
            world.randomNumber = randomWorld(world.randomNumber);
            result.add(world);
        }
        result.sort(WORLD_COMPARATOR);
        for (World world : result) {
            dbClient.execute()
                    .createUpdate(UPDATE_WORLD)
                    .addParam(world.randomNumber)
                    .addParam(world.id)
                    .execute();
        }
        return result;
    }

    @Override
    public List<Fortune> getFortunes() {
        try (Stream<DbRow> rows = dbClient.execute().createQuery(SELECT_FORTUNES).execute()) {
            return rows.map(HelidonDbClientRepository::mapFortune).collect(Collectors.toList());
        }
    }

    private static World mapWorld(DbRow row) {
        return new World(row.column("id").get(Integer.class), row.column("randomnumber").get(Integer.class));
    }

    private static Fortune mapFortune(DbRow row) {
        return new Fortune(row.column("id").get(Integer.class), row.column("message").get(String.class));
    }
}
