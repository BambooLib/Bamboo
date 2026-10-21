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

        val settings = configuration.poolSettings

        val maxPoolSize = settings.maximumPoolSize
            ?: when (configuration.type) {
                DatabaseType.SQLITE -> 1
                else -> 10
            }

        val minIdle = settings.minimumIdle
            ?: minOf(1, maxPoolSize)

        val connectionTimeout = settings.connectionTimeoutMillis
            ?: 30_000L

        val validationTimeout = settings.validationTimeoutMillis
            ?: 5_000L

        val initializationFailTimeout = settings.initializationFailTimeoutMillis
            ?: 1L

        require(minIdle <= maxPoolSize) {
            "Minimum idle connections cannot exceed maximum pool size."
        }

        require(validationTimeout < connectionTimeout) {
            "Validation timeout must be less than connection timeout."
        }

        if (configuration.type == DatabaseType.SQLITE &&
            configuration.jdbcUrl.equals("jdbc:sqlite::memory", ignoreCase = true) &&
            maxPoolSize > 1
        ) {
            throw IllegalArgumentException(
                "A private in-memory SQLite database requires a maximum pool size of 1." +
                "Use a shared-cache in-memory URL " +
                "to share the database between connections."
            )
        }

        val config = HikariConfig().apply {
            poolName = createPoolName(name)
            jdbcUrl = configuration.jdbcUrl

            configuration.username?.let {
                username = it
            }

            configuration.password?.let {
                password = it
            }

            maximumPoolSize = maxPoolSize
            minimumIdle = minIdle
            this.connectionTimeout = connectionTimeout
            this.validationTimeout = validationTimeout
            this.initializationFailTimeout = initializationFailTimeout

            settings.idleTimeoutMillis?.let {
                idleTimeout = it
            }

            settings.maxLifetimeMillis?.let {
                maxLifetime = it
            }

            settings.keepaliveTimeMillis?.let {
                keepaliveTime = it
            }

            settings.leakDetectionThresholdMillis?.let {
                leakDetectionThreshold = it
            }

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