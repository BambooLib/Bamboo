package io.github.deopping.bamboo.database.internal.sql

import com.zaxxer.hikari.HikariDataSource
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.DatabaseState
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.sql.SqlDatabase
import io.github.deopping.bamboo.database.api.sql.SqlPoolSettings
import io.github.deopping.bamboo.database.internal.DatabaseManagerImpl
import io.github.deopping.bamboo.database.internal.configurations.SqlDatabaseConfiguration
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDatabaseIntegrationTest {

    @Test
    fun `SQLite in-memory database supports statements and transactions`() {
        val configuration = SqlDatabaseConfiguration(
            type = DatabaseType.SQLITE,
            jdbcUrl = "jdbc:sqlite::memory:"
        )

        exerciseDatabase(
            name = "sqlite-test",
            configuration = configuration
        )
    }

    @Test
    fun `H2 in-memory database supports statements and transactions`() {
        val databaseName = "bamboo_test_${
            UUID.randomUUID().toString().replace("-", "")
        }"

        val configuration = DatabaseConfiguration.h2(
            jdbcUrl = "jdbc:h2:mem:$databaseName"
        )

        exerciseDatabase(
            name = databaseName,
            configuration = configuration
        )
    }

    @Test
    fun `custom pool settings are applied`() {
        val databaseName = "bamboo_pool_test"

        val configuration = DatabaseConfiguration.h2(
            jdbcUrl = "jdbc:h2:mem:$databaseName",
            poolSettings = SqlPoolSettings(
                maximumPoolSize = 3,
                minimumIdle = 1,
                connectionTimeoutMillis = 10_000L,
                validationTimeoutMillis = 2_000L
            )
        )

        val manager = DatabaseManagerImpl(SqlDatabaseFactory())

        manager.use { manager ->
            val database = manager.connect(
                name = databaseName,
                configuration = configuration
            ) as SqlDatabase

            val dataSource = database.dataSource as HikariDataSource

            assertEquals(3, dataSource.maximumPoolSize)
            assertEquals(1, dataSource.minimumIdle)
            assertEquals(10_000L, dataSource.connectionTimeout)
            assertEquals(2_000L, dataSource.validationTimeout)
        }
    }

    private fun exerciseDatabase(
        name: String,
        configuration: DatabaseConfiguration.Sql
    ) {
        val manager = DatabaseManagerImpl(SqlDatabaseFactory())

        val database = try {
            manager.connect(
                name = name,
                configuration = configuration,
                schema = null
            ) as? SqlDatabase
                ?: error("The SQL factory returned a non-SQL database.")
        } catch (failure: Throwable) {
            manager.close()
            throw failure
        }

        manager.use { _ ->
            assertEquals(DatabaseState.CONNECTED, database.state)
            assertTrue(database.ping())

            // DDL should work through common execute API.
            database.execute(
                """
                        CREATE TABLE bamboo_test (
                            id INTEGER PRIMARY KEY,
                            label VARCHAR(100) NOT NULL
                        )
                    """.trimIndent()
            )

            // A successful transaction should commit its changes.
            val affectedRows = database.transaction { connection ->
                connection.prepareStatement(
                    "INSERT INTO bamboo_test (id, label) VALUES (?, ?)"
                ).use { statement ->
                    statement.setInt(1, 1)
                    statement.setString(2, "committed")
                    statement.executeUpdate()
                }
            }

            assertEquals(1, affectedRows)
            assertEquals(1, countRows(database))

            assertThrows(DatabaseException::class.java) {
                database.transaction<Int> { connection ->
                    connection.prepareStatement(
                        "INSERT INTO bamboo_test (id, label) VALUES (?, ?)"
                    ).use { statement ->
                        statement.setInt(1, 2)
                        statement.setString(2, "rolled-back")
                        statement.executeUpdate()
                    }

                    throw IllegalStateException("Rollback test")
                }
            }

            assertEquals(1, countRows(database))
            assertTrue(database.ping())
        }

        assertEquals(DatabaseState.CLOSED, database.state)
        assertFalse(database.ping())

        assertThrows(DatabaseException::class.java) {
            database.execute("SELECT 1")
        }
    }

    private fun countRows(database: SqlDatabase): Int =
        database.dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(
                    "SELECT COUNT(*) FROM bamboo_test"
                ).use { resultSet ->
                    check(resultSet.next())
                    resultSet.getInt(1)
                }
            }
        }

}