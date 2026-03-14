package io.koraframework.benchmark.repository;

import ru.tinkoff.kora.common.Module;
import ru.tinkoff.kora.database.jdbc.mapper.parameter.JdbcParameterColumnMapper;

import java.util.List;

@Module
public interface MapperModule {

    default JdbcParameterColumnMapper<List<Integer>> postgresListOfIntegerJdbcParameterColumnMapper() {
        return (stmt, index, value) -> {
            var typeArray = value.toArray(Integer[]::new);
            var sqlArray = stmt.getConnection().createArrayOf("INT", typeArray);
            stmt.setArray(index, sqlArray);
        };
    }
}
