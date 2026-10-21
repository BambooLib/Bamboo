package io.github.deopping.bamboo.database.internal.schema

import io.github.deopping.bamboo.database.api.migration.DatabaseMigrationAction
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.api.schema.DatabaseSchemaBuilder
import io.github.deopping.bamboo.database.internal.migration.DatabaseMigrationImpl

internal class DatabaseSchemaBuilderImpl : DatabaseSchemaBuilder {

    private var version: Int? = null

    private val migrations = linkedMapOf<Int, DatabaseMigrationImpl>()

    override fun version(version: Int) {
        require(version >= 0) {
            "Database schema version cannot be negative."
        }

        this.version = version
    }

    override fun migration(
        from: Int,
        to: Int,
        action: DatabaseMigrationAction
    ) {
        require(from >= 0) {
            "Migration source version cannot be negative."
        }

        require(to > from) {
            "Migration target version must be greater than its source version."
        }

        require(from !in migrations) {
            "A migration from version $from has already been defined."
        }

        migrations[from] = DatabaseMigrationImpl(
            from = from,
            to = to,
            action = action
        )
    }

    override fun build(): DatabaseSchema {
        val schemaVersion = version
            ?: throw IllegalStateException(
                "Database schema version has not been configured."
            )

        migrations.values.forEach { migration ->
            require(migration.to <= schemaVersion) {
                "Migration ${migration.from} -> ${migration.to} exceeds " +
                "schema version $schemaVersion."
            }
        }

        return DatabaseSchemaImpl(
            version = schemaVersion,
            migrations = migrations
        )
    }

}