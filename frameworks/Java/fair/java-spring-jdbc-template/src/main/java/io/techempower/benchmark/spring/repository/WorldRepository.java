package io.techempower.benchmark.spring.repository;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.World;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorldRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<World> WORLD_ROW_MAPPER = (rs, rowNum) ->
            new World(rs.getInt("id"), rs.getInt("randomnumber"));

    private static final RowMapper<Fortune> FORTUNE_ROW_MAPPER = (rs, rowNum) ->
            new Fortune(rs.getInt("id"), rs.getString("message"));

    public WorldRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public World findById(int id) {
        return jdbcTemplate.queryForObject(
                "SELECT id, randomnumber FROM world WHERE id = ?",
                WORLD_ROW_MAPPER,
                id
        );
    }

    public int findRandomNumberById(int id) {
        return jdbcTemplate.queryForObject(
                "SELECT randomnumber FROM world WHERE id = ?",
                Integer.class,
                id
        );
    }

    public void update(World world) {
        jdbcTemplate.update(
                "UPDATE world SET randomnumber = ? WHERE id = ?",
                world.randomNumber(),
                world.id()
        );
    }

    public List<Fortune> findAllFortunes() {
        return jdbcTemplate.query(
                "SELECT id, message FROM fortune",
                FORTUNE_ROW_MAPPER
        );
    }
}
