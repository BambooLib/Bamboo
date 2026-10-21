package io.github.deopping.bamboo.database.internal.sql

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import io.github.deopping.bamboo.database.api.schema.migrate
import io.github.deopping.bamboo.database.api.sql.SqlDatabase
import io.github.deopping.bamboo.database.internal.DatabaseManagerImpl
import io.github.deopping.bamboo.database.internal.configurations.SqlDatabaseConfiguration
import io.github.deopping.bamboo.database.internal.schema.DatabaseSchemaBuilderImpl
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.UUID
import kotlin.test.assertEquals

class SqlMigrationTest {

    @Test
    fun `SQLite migrations are applied and recorded`() {
        verifyMigrations(
            name = "migration-sqlite",
            configuration = DatabaseConfiguration.sqlite(
                Path.of("ignored-for-memory.db")
            ).let {
                SqlDatabaseConfiguration(
                    type = DatabaseType.SQLITE,
                    jdbcUrl = "jdbc:sqlite::memory:"
                )
            }
        )
    }

    @Test
    fun `H2 migrations are applied and recorded`() {
        val name = "migration-h2-${
            UUID.randomUUID().toString().replace("-", "")
        }"

        verifyMigrations(
            name = name,
            configuration = DatabaseConfiguration.h2(
                jdbcUrl = "jdbc:h2:mem:$name"
            )
        )
    }

    private fun migrationSchema(): DatabaseSchema =
        DatabaseSchemaBuilderImpl().apply {
            version(2)

            migrate(0, 1) {
                (this as SqlDatabase).execute(
                    """
                        CREATE TABLE migration_test (
                            id INTEGER PRIMARY KEY
                        )
                    """.trimIndent()
                )
            }

            migrate(1, 2) {
                (this as SqlDatabase).execute(
                    "ALTER TABLE migration_test ADD COLUMN label VARCHAR(100)"
                )
            }
        }.build()

    private fun verifyMigrations(
        name: String,
        configuration: DatabaseConfiguration.Sql
    ) {
        val manager = DatabaseManagerImpl(SqlDatabaseFactory())

        manager.use { manager ->
            val database = manager.connect(
                name = name,
                configuration = configuration,
                schema = migrationSchema()
            ) as SqlDatabase

            assertEquals(2, readSchemaVersion(database, name))

            SqlDatabaseMigrationRunner.apply(database, migrationSchema())

            database.execute("INSERT INTO migration_test (id, label) VALUES (1, 'migrated')")

            val label = database.dataSource.connection.use { connection ->
                connection.prepareStatement(
                    "SELECT label FROM migration_test WHERE id = 1"
                ).use { statement ->
                    statement.executeQuery().use { resultSet ->
                        check(resultSet.next())
                        resultSet.getString("label")
                    }
                }
            }

            assertEquals("migrated", label)
            assertEquals(2, readSchemaVersion(database, name))
        }
    }

    private fun readSchemaVersion(
        database: SqlDatabase,
        name: String
    ): Int =
        database.dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                    SELECT schema_version
                    FROM bamboo_schema_versions
                    WHERE database_name = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, name)

                statement.executeQuery().use { resultSet ->
                    check(resultSet.next())
                    resultSet.getInt("schema_version")
                }
            }
        }

}