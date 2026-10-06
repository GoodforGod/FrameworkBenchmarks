package io.koraframework.benchmark.repository;

import io.koraframework.common.Module;
import io.koraframework.database.jdbc.mapper.parameter.JdbcParameterColumnMapper;

import java.util.Collection;

@Module
public interface MapperModule {

    default JdbcParameterColumnMapper<Collection<Integer>> postgresCollectionOfIntegerJdbcParameterColumnMapper() {
        return (stmt, index, value) -> {
            var typeArray = value.toArray(Integer[]::new);
            var sqlArray = stmt.getConnection().createArrayOf("INT", typeArray);
            stmt.setArray(index, sqlArray);
        };
    }
}
