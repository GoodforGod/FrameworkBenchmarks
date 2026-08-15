package io.techempower.benchmark.quarkus.repository;

import io.techempower.benchmark.quarkus.model.Fortune;
import io.techempower.benchmark.quarkus.model.World;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.List;

import static org.jooq.SQLDialect.POSTGRES;

@ApplicationScoped
public class WorldRepository {

    private final DataSource dataSource;

    public WorldRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public World findWorld(int id) {
        return context().select(DSL.field("id", Integer.class), DSL.field("randomnumber", Integer.class))
                .from("world")
                .where(DSL.field("id", Integer.class).eq(id))
                .fetchOne(record -> new World(record.value1(), record.value2()));
    }

    public void updateRandomNumber(int id, int randomNumber) {
        context().update(DSL.table("world"))
                .set(DSL.field("randomnumber", Integer.class), randomNumber)
                .where(DSL.field("id", Integer.class).eq(id))
                .execute();
    }

    public List<Fortune> findAllFortunes() {
        return context().select(DSL.field("id", Integer.class), DSL.field("message", String.class))
                .from("fortune")
                .fetch(record -> new Fortune(record.value1(), record.value2()));
    }

    private DSLContext context() {
        return DSL.using(dataSource, POSTGRES);
    }
}
