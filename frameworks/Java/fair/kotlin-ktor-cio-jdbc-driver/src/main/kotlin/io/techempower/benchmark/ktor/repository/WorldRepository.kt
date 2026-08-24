package io.techempower.benchmark.ktor.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.techempower.benchmark.ktor.model.Fortune
import io.techempower.benchmark.ktor.model.World
import io.techempower.benchmark.ktor.util.QueryUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection

class WorldRepository {
    private val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = env("POSTGRES_JDBC_URL", "jdbc:postgresql://localhost:5432/postgres")
        username = env("POSTGRES_USER", "postgres")
        password = env("POSTGRES_PASS", "postgres")
        driverClassName = "org.postgresql.Driver"
        poolName = "ktor-jdbc"
        minimumIdle = 16
        maximumPoolSize = 512
        connectionTimeout = 10_000
        validationTimeout = 5_000
        idleTimeout = 0
        maxLifetime = 0
        initializationFailTimeout = 10_000
        addDataSourceProperty("preparedStatementCacheQueries", "512")
        addDataSourceProperty("preparedStatementCacheSizeMiB", "16")
        addDataSourceProperty("prepareThreshold", "1")
        addDataSourceProperty("loggerLevel", "OFF")
        addDataSourceProperty("sslmode", "disable")
        addDataSourceProperty("disableColumnSanitiser", "true")
    })

    suspend fun findWorld(id: Int): World = withContext(Dispatchers.IO) {
        connection().use { connection ->
            findWorld(connection, id)
        }
    }

    suspend fun findWorlds(count: Int): List<World> = withContext(Dispatchers.IO) {
        connection().use { connection ->
            val worlds = ArrayList<World>(count)
            repeat(count) {
                worlds.add(findWorld(connection, QueryUtils.randomWorld()))
            }
            worlds
        }
    }

    suspend fun updateWorlds(count: Int): List<World> = withContext(Dispatchers.IO) {
        connection().use { connection ->
            connection.autoCommit = false
            try {
                val worlds = ArrayList<World>(count)
                repeat(count) {
                    val oldWorld = findWorld(connection, QueryUtils.randomWorld())
                    worlds.add(World(oldWorld.id, QueryUtils.randomWorld(oldWorld.randomNumber)))
                }
                worlds.sortBy(World::id)
                updateWorlds(connection, worlds)
                connection.commit()
                worlds
            } catch (e: Exception) {
                connection.rollback()
                throw e
            }
        }
    }

    suspend fun findFortunes(): List<Fortune> = withContext(Dispatchers.IO) {
        connection().use { connection ->
            connection.prepareStatement("SELECT id, message FROM fortune").use { statement ->
                statement.executeQuery().use { resultSet ->
                    val fortunes = ArrayList<Fortune>()
                    while (resultSet.next()) {
                        fortunes.add(Fortune(resultSet.getInt(1), resultSet.getString(2)))
                    }
                    fortunes
                }
            }
        }
    }

    private fun connection(): Connection = dataSource.connection

    private fun findWorld(connection: Connection, id: Int): World {
        connection.prepareStatement("SELECT id, randomnumber FROM world WHERE id = ?").use { statement ->
            statement.setInt(1, id)
            statement.executeQuery().use { resultSet ->
                resultSet.next()
                return World(resultSet.getInt(1), resultSet.getInt(2))
            }
        }
    }

    private fun updateWorlds(connection: Connection, worlds: List<World>) {
        connection.prepareStatement("UPDATE world SET randomnumber = ? WHERE id = ?").use { statement ->
            for (world in worlds) {
                statement.setInt(1, world.randomNumber)
                statement.setInt(2, world.id)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }

    private fun env(name: String, fallback: String): String {
        val value = System.getenv(name)
        return if (value.isNullOrBlank()) fallback else value
    }
}
