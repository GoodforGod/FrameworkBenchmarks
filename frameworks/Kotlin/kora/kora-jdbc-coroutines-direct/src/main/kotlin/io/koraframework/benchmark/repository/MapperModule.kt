package io.koraframework.benchmark.repository

import ru.tinkoff.kora.common.Module
import ru.tinkoff.kora.database.jdbc.mapper.parameter.JdbcParameterColumnMapper

@Module
interface MapperModule {

    fun postgresCollectionOfIntJdbcParameterColumnMapper(): JdbcParameterColumnMapper<Collection<Int>> {
        return JdbcParameterColumnMapper { stmt, index, value ->
            val typeArray = value.toTypedArray()
            val sqlArray = stmt.connection.createArrayOf("INT", typeArray)
            stmt.setArray(index, sqlArray)
        }
    }
}
