package io.github.deopping.bamboo.database.internal.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseType

internal object SqlDataSourceFactory {

    fun create(
        name: String,
        configuration: DatabaseConfiguration.Sql
    ): HikariDataSource {
        validateConfiguration(configuration)

        val config = HikariConfig().apply {
            poolName = createPoolName(name)
            jdbcUrl = configuration.jdbcUrl

            configuration.username?.let {
                username = it
            }

            configuration.password?.let {
                password = it
            }

            // SQLite is embedded and benefits from conservative
            // concurrency defaults. Remote databases can use a
            // larger pool.
            maximumPoolSize = when (configuration.type) {
                DatabaseType.SQLITE -> 1
                else -> 10
            }

            minimumIdle = 1

            connectionTimeout = 30_000
            validationTimeout = 5_000
            initializationFailTimeout = 1

            configuration.properties.forEach { (key, value) ->
                require(key.isNotBlank()) {
                    "JDBC property names must not be blank."
                }

                if (value != null) {
                    addDataSourceProperty(key, value)
                }
            }
        }

        return HikariDataSource(config)
    }

    private fun validateConfiguration(
        configuration: DatabaseConfiguration.Sql
    ) {
        val expectedPrefix = when (configuration.type) {
            DatabaseType.SQLITE -> "jdbc:sqlite:"
            DatabaseType.H2 -> "jdbc:h2:"
            DatabaseType.MYSQL -> "jdbc:mysql:"
            DatabaseType.MARIADB -> "jdbc:mariadb:"
            else -> throw IllegalArgumentException(
                "Unsupported SQL database type: ${configuration.type}."
            )
        }

        require(
            configuration.jdbcUrl.startsWith(
                expectedPrefix,
                ignoreCase = true
            )
        ) {
            "JDBC URL does not match database type " +
            "'${configuration.type}'"
        }
    }

    private fun createPoolName(name: String): String {
        val safeName = name
            .replace(Regex("[^A-Za-z0-9_.-]"), "_")
            .take(55)

        return "Bamboo-$safeName"
    }

}