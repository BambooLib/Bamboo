package io.github.deopping.bamboo.database.internal.sql

import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.internal.DatabaseFactory

internal class SqlDatabaseFactory : DatabaseFactory {

    override fun create(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database {
        require(name.isNotBlank()) {
            "Database name must not be blank."
        }

        val sqlConfiguration = configuration as? DatabaseConfiguration.Sql
            ?: throw DatabaseException(
                "Configuration for database '$name' is not an SQL configuration."
            )

        if (sqlConfiguration.type.family != DatabaseFamily.SQL) {
            throw DatabaseException(
                "Database '$name' has a non-SQL database type."
            )
        }

        val dataSource = try {
            SqlDataSourceFactory.create(name, sqlConfiguration)
        } catch (failure: Exception) {
            throw DatabaseException(
                "Failed to create the SQL datasource for database '$name'.",
                failure
            )
        }

        val database = SqlDatabaseImpl(
            name = name,
            type = sqlConfiguration.type,
            dataSource = dataSource
        )

        database.initialize()

        try {
            if (schema != null) {
                SqlDatabaseMigrationRunner.apply(database, schema)
            }
        } catch (failure: Throwable) {
            try {
                database.close()
            } catch (closeFailure: Throwable) {
                failure.addSuppressed(closeFailure)
            }

            when (failure) {
                is Error -> throw failure
                is DatabaseException -> throw failure
                else -> throw DatabaseException(
                    "Failed to apply the schema to database '$name'.",
                    failure
                )
            }
        }

        return database
    }

}