package io.github.deopping.bamboo.database.api

import io.github.deopping.bamboo.database.internal.configurations.MongoDatabaseConfiguration
import io.github.deopping.bamboo.database.internal.configurations.RedisDatabaseConfiguration
import io.github.deopping.bamboo.database.internal.configurations.SqlDatabaseConfiguration
import java.nio.file.Path

/**
 * Describes how Bamboo should connect to a database.
 *
 * Implementations are provided for each supported database family.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseConfiguration {

    /**
     * The database type represented by this configuration.
     * @since 0.1.0
     */
    val type: DatabaseType

    interface Sql : DatabaseConfiguration {
        val jdbcUrl: String
        val username: String?
        val password: String?
        val properties: Map<String, Any?>
    }

    interface Mongo : DatabaseConfiguration {
        val connectionString: String
        val database: String
    }

    interface Redis : DatabaseConfiguration {
        val connectionString: String
    }

    companion object {

        /**
         * Creates an SQLite configuration.
         *
         * @param file SQLite database file
         * @return SQLite configuration
         * @since 0.1.0
         */
        @JvmStatic
        fun sqlite(file: Path): Sql {
            return SqlDatabaseConfiguration(
                type = DatabaseType.SQLITE,
                jdbcUrl = "jdbc:sqlite:${file.toAbsolutePath()}"
            )
        }

        /**
         * Creates an H2 configuration.
         *
         * @param jdbcUrl H2 JDBC URL
         * @param username optional database username
         * @param password optional database password
         * @return H2 configuration
         * @since 0.1.0
         */
        @JvmStatic
        fun h2(
            jdbcUrl: String,
            username: String? = null,
            password: String? = null
        ): Sql {
            return SqlDatabaseConfiguration(
                type = DatabaseType.H2,
                jdbcUrl = jdbcUrl,
                username = username,
                password = password
            )
        }

        /**
         * Creates a MySQL configuration.
         *
         * @param jdbcUrl MySQL JDBC URL
         * @param username database username
         * @param password database password
         * @return MySQL configuration
         * @since 0.1.0
         */
        @JvmStatic
        fun mysql(
            jdbcUrl: String,
            username: String,
            password: String
        ): Sql {
            return SqlDatabaseConfiguration(
                type = DatabaseType.MYSQL,
                jdbcUrl = jdbcUrl,
                username = username,
                password = password
            )
        }

        /**
         * Creates a MariaDB configuration.
         *
         * @param jdbcUrl MariaDB JDBC URL
         * @param username database username
         * @param password database password
         * @return MariaDB configuration
         * @since 0.1.0
         */
        @JvmStatic
        fun mariadb(
            jdbcUrl: String,
            username: String,
            password: String
        ): Sql {
            return SqlDatabaseConfiguration(
                type = DatabaseType.MARIADB,
                jdbcUrl = jdbcUrl,
                username = username,
                password = password
            )
        }

        /**
         * Creates a MongoDB configuration.
         *
         * @param connectionString MongoDB connection string
         * @param database database name
         * @return MongoDB configuration
         * @since 0.1.0
         */
        @JvmStatic
        fun mongodb(
            connectionString: String,
            database: String
        ): Mongo {
            return MongoDatabaseConfiguration(
                connectionString = connectionString,
                database = database
            )
        }

        /**
         * Creates a Redis configuration.
         *
         * @param connectionString Redis connection string
         * @return Redis configuration
         * @since 0.1.0
         */
        @JvmStatic
        fun redis(
            connectionString: String
        ): Redis {
            return RedisDatabaseConfiguration(
                connectionString = connectionString
            )
        }

    }

}