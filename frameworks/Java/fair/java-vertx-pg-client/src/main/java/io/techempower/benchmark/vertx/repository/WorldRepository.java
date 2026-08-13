package io.techempower.benchmark.vertx.repository;

import io.techempower.benchmark.vertx.model.Fortune;
import io.techempower.benchmark.vertx.model.World;
import io.techempower.benchmark.vertx.util.QueryUtils;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class WorldRepository {

    private static final String FIND_WORLD = "SELECT id, randomnumber FROM world WHERE id = $1";
    private static final String FIND_FORTUNES = "SELECT id, message FROM fortune";
    private static final String UPDATE_WORLD = "UPDATE world SET randomnumber = $1 WHERE id = $2";
    private static final Comparator<World> WORLD_COMPARATOR = Comparator.comparingInt(World::getId);

    private final Pool pool;

    public WorldRepository(Pool pool) {
        this.pool = pool;
    }

    public Future<World> findWorld(int id) {
        return pool.withConnection(connection -> findWorld(connection, id));
    }

    public Future<List<World>> findWorlds(int count) {
        return pool.withConnection(connection -> findWorlds(connection, count));
    }

    public Future<List<World>> updateWorlds(int count) {
        return pool.withTransaction(connection -> findWorlds(connection, count)
                .compose(worlds -> {
                    for (World world : worlds) {
                        world.setRandomNumber(QueryUtils.randomWorld(world.getRandomNumber()));
                    }
                    worlds.sort(WORLD_COMPARATOR);
                    return updateWorlds(connection, worlds);
                }));
    }

    public Future<List<Fortune>> findFortunes() {
        return pool.withConnection(connection -> connection.preparedQuery(FIND_FORTUNES).execute()
                .map(rows -> {
                    List<Fortune> fortunes = new ArrayList<>(rows.size());
                    for (Row row : rows) {
                        fortunes.add(new Fortune(row.getInteger(0), row.getString(1)));
                    }
                    return fortunes;
                }));
    }

    private static Future<List<World>> findWorlds(SqlConnection connection, int count) {
        List<World> worlds = new ArrayList<>(count);
        Future<List<World>> chain = Future.succeededFuture(worlds);
        for (int i = 0; i < count; i++) {
            chain = chain.compose(current -> findWorld(connection, QueryUtils.randomWorld())
                    .map(world -> {
                        current.add(world);
                        return current;
                    }));
        }
        return chain;
    }

    private static Future<World> findWorld(SqlConnection connection, int id) {
        return connection.preparedQuery(FIND_WORLD)
                .execute(Tuple.of(id))
                .map(rows -> {
                    Row row = rows.iterator().next();
                    return new World(row.getInteger(0), row.getInteger(1));
                });
    }

    private static Future<List<World>> updateWorlds(SqlConnection connection, List<World> worlds) {
        Future<List<World>> chain = Future.succeededFuture(worlds);
        for (World world : worlds) {
            chain = chain.compose(current -> connection.preparedQuery(UPDATE_WORLD)
                    .execute(Tuple.of(world.getRandomNumber(), world.getId()))
                    .map(current));
        }
        return chain;
    }
}
