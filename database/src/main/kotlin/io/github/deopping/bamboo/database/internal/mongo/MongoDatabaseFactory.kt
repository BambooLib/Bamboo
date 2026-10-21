package io.github.deopping.bamboo.database.internal.mongo

import com.mongodb.client.MongoClients
import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.internal.DatabaseFactory
import io.github.deopping.bamboo.database.internal.DatabaseFailures

internal class MongoDatabaseFactory : DatabaseFactory {

    override fun create(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database {
        val mongoConfiguration = configuration as? DatabaseConfiguration.Mongo
            ?: throw DatabaseFailures.create(
                "Configuration for database '$name' is not a MongoDB configuration."
            )

        if (mongoConfiguration.type != DatabaseType.MONGODB) {
            throw DatabaseFailures.create(
                "Invalid database type for MongoDB database '$name'."
            )
        }

        if (mongoConfiguration.database.isBlank()) {
            throw DatabaseFailures.create(
                "MongoDB database name must not be blank."
            )
        }

        // TODO MongoDB schema migrations
        if (schema != null) {
            throw DatabaseFailures.create(
                "Database migrations are not yet implemented for MongoDB."
            )
        }

        val client = try {
            MongoClients.create(mongoConfiguration.connectionString)
        } catch (failure: Exception) {
            throw DatabaseFailures.create(
                "Failed to create the MongoDB client for '$name'.",
                failure
            )
        }

        val database = MongoDatabaseImpl(
            name = name,
            client = client,
            mongoDatabase = client.getDatabase(mongoConfiguration.database)
        )

        try {
            database.initialize()
            return database
        } catch (failure: Exception) {
            try {
                database.close()
            } catch (closeFailure: Exception) {
                failure.addSuppressed(closeFailure)
            }

            throw failure
        }
    }

}