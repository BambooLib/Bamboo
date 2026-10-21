package io.github.deopping.bamboo.database.internal

import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.internal.mongo.MongoDatabaseFactory
import io.github.deopping.bamboo.database.internal.redis.RedisDatabaseFactory
import io.github.deopping.bamboo.database.internal.sql.SqlDatabaseFactory

internal class CompositeDatabaseFactory(
    private val sqlFactory: DatabaseFactory = SqlDatabaseFactory(),
    private val mongoFactory: DatabaseFactory = MongoDatabaseFactory(),
    private val redisFactory: DatabaseFactory = RedisDatabaseFactory()
) : DatabaseFactory {

    override fun create(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database =
        when (configuration) {
            is DatabaseConfiguration.Sql ->
                sqlFactory.create(name, configuration, schema)

            is DatabaseConfiguration.Mongo ->
                mongoFactory.create(name, configuration, schema)

            is DatabaseConfiguration.Redis ->
                redisFactory.create(name, configuration, schema)

            else -> throw DatabaseException(
                "Unsupported database configuration for '$name'."
            )
        }

}