package io.github.deopping.bamboo.database.internal.redis

import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.internal.DatabaseFactory
import io.github.deopping.bamboo.database.internal.DatabaseFailures
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection

internal class RedisDatabaseFactory : DatabaseFactory {

    override fun create(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database {
        val redisConfiguration = configuration as? DatabaseConfiguration.Redis
            ?: throw DatabaseFailures.create(
                "Configuration for database '$name' is not a Redis configuration."
            )

        if (redisConfiguration.type != DatabaseType.REDIS) {
            throw DatabaseFailures.create(
                "Invalid database type for Redis database '$name'."
            )
        }

        // TODO implement Redis schema migrations
        if (schema != null) {
            throw DatabaseFailures.create(
                "Database migrations are not yet implemented for Redis."
            )
        }

        val client = try {
            RedisClient.create(redisConfiguration.connectionString)
        } catch (failure: Exception) {
            throw DatabaseFailures.create(
                "Failed to create the Redis client for '$name'.",
                failure
            )
        }

        var connection: StatefulRedisConnection<String, String>? = null

        try {
            connection = client.connect()

            val database = RedisDatabaseImpl(
                name = name,
                client = client,
                connection = connection
            )

            database.initialize()
            return database
        } catch (failure: Exception) {
            try {
                connection?.close()
            } catch (closeFailure: Exception) {
                failure.addSuppressed(closeFailure)
            }

            try {
                client.shutdown()
            } catch (shutdownFailure: Exception) {
                failure.addSuppressed(shutdownFailure)
            }

            if (failure is DatabaseException) {
                throw failure
            }

            throw DatabaseFailures.create(
                "Failed to initialize Redis database '$name'.",
                failure
            )
        }
    }

}