package io.github.deopping.bamboo.database.internal.sql

import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.migration.DatabaseMigration
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.api.sql.SqlDatabase
import io.github.deopping.bamboo.database.internal.migration.ManagedDatabaseMigration
import java.sql.Connection
import java.sql.SQLException

internal object SqlDatabaseMigrationRunner {

    private const val VERSION_TABLE = "bamboo_schema_versions"

    private const val CREATE_VERSION_TABLE = """
        CREATE TABLE IF NOT EXISTS $VERSION_TABLE (
            database_name VARCHAR(255) NOT NULL PRIMARY KEY,
            schema_version INTEGER NOT NULL
        )
    """

    fun apply(
        database: SqlDatabase,
        schema: DatabaseSchema
    ) {
        validateSchema(schema)

        if (database.name.length > 255) {
            throw DatabaseException(
                "Database name exceeds the 255-character schema tracking limit."
            )
        }

        database.execute(CREATE_VERSION_TABLE)
        ensureVersionRecord(database)

        var currentVersion = readVersion(database, database.name)
            ?: throw DatabaseException(
                "Schema version record could not be initialized."
            )

        if (currentVersion > schema.version) {
            throw DatabaseException(
                "Database '${database.name}' is at schema version " +
                "$currentVersion, which is newer than the configured " +
                "version ${schema.version}. Automatic downgrades are not supported."
            )
        }

        while (currentVersion < schema.version) {
            val migration = schema.migrations[currentVersion]
                ?: throw DatabaseException(
                    "No migration exists from schema version $currentVersion to " +
                    "${schema.version} for database '${database.name}'."
                )

            val managedMigration = migration as? ManagedDatabaseMigration
                ?: throw DatabaseException(
                    "Migration ${migration.from} -> ${migration.to} was not " +
                    "created by Bamboo's schema builder."
                )

            currentVersion = applyMigration(
                database = database,
                migration = migration,
                action = managedMigration,
                expectedVersion = currentVersion
            )
        }
    }

    private fun validateSchema(schema: DatabaseSchema) {
        if (schema.version < 0) {
            throw DatabaseException(
                "Database schema version cannot be negative."
            )
        }

        for ((key, migration) in schema.migrations) {
            if (key != migration.from ||
                migration.from < 0 ||
                migration.to <= migration.from ||
                migration.to > schema.version
            ) {
                throw DatabaseException(
                    "Invalid database migration ${migration.from} -> ${migration.to} in " +
                    "schema version ${schema.version}."
                )
            }
        }
    }

    private fun ensureVersionRecord(database: SqlDatabase) {
        database.transaction { connection ->
            if (readVersion(connection, database.name) == null) {
                try {
                    connection.prepareStatement(
                        """
                            INSERT INTO $VERSION_TABLE
                                (database_name, schema_version)
                            VALUES (?, 0)
                        """.trimIndent()
                    ).use { statement ->
                        statement.setString(1, database.name)
                        statement.executeUpdate()
                    }
                } catch (failure: SQLException) {
                    // Another initializer may have inserted this record
                    // after our initial SELECT
                    if (readVersion(connection, database.name) == null) {
                        throw failure
                    }
                }
            }
        }
    }

    private fun applyMigration(
        database: SqlDatabase,
        migration: DatabaseMigration,
        action: ManagedDatabaseMigration,
        expectedVersion: Int
    ): Int {
        return database.transaction { connection ->
            // Acquire the version row's write lock before inspecting its
            // current value. The row is guaranteed to exist at this point.
            connection.prepareStatement(
                """
                    UPDATE $VERSION_TABLE
                    SET schema_version = schema_version
                    WHERE database_name = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, database.name)
                statement.executeUpdate()
            }

            val storedVersion = readVersion(connection, database.name)
                ?: throw DatabaseException(
                    "Schema version record disappeared for '${database.name}'."
                )

            if (storedVersion != expectedVersion) {
                throw DatabaseException(
                    "Schema version for '${database.name}' changed during" +
                    "migration. Expected $expectedVersion but found $storedVersion."
                )
            }

            val migrationDatabase = MigrationScopedSqlDatabase(
                delegate = database,
                connection = connection
            )

            action.action.migrate(migrationDatabase)

            val updatedRows = connection.prepareStatement(
                """
                    UPDATE $VERSION_TABLE
                    SET schema_version = ?
                    WHERE database_name = ? AND schema_version = ?
                """.trimIndent()
            ).use { statement ->
                statement.setInt(1, migration.to)
                statement.setString(2, database.name)
                statement.setInt(3, expectedVersion)
                statement.executeUpdate()
            }

            if (updatedRows != 1) {
                throw DatabaseException(
                    "Failed to record schema version ${migration.to} " +
                    "for database '${database.name}'."
                )
            }

            migration.to
        }
    }

    private fun readVersion(
        database: SqlDatabase,
        name: String
    ): Int? = database.dataSource.connection.use { connection ->
        readVersion(connection, name)
    }

    private fun readVersion(
        connection: Connection,
        name: String
    ): Int? {
        return connection.prepareStatement(
            """
                SELECT schema_version FROM $VERSION_TABLE
                WHERE database_name = ?
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, name)

            statement.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    resultSet.getInt("schema_version")
                } else {
                    null
                }
            }
        }
    }

}