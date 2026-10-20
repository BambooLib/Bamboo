package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class DatabaseConfigurationRedactionTest {

    @Test
    fun `SQL configuration redacts connection details`() {
        val jdbcUrl = "jdbc:mysql://db.example.test/app?token=url-secret"

        val username = "private-user"
        val password = "private-password"
        val passwordProperty = "driver-secret"
        val tokenProperty = "property-secret"

        val configuration = SqlDatabaseConfiguration(
            type = DatabaseType.MYSQL,
            jdbcUrl = jdbcUrl,
            username = username,
            password = password,
            properties = mapOf(
                "password" to passwordProperty,
                "token" to tokenProperty
            )
        )

        val representation = configuration.toString()

        assertTrue(representation.contains("MYSQL"))
        assertRedacted(
            representation,
            jdbcUrl,
            username,
            password,
            passwordProperty,
            tokenProperty
        )
    }

    @Test
    fun `MongoDB configuration redacts its connection string`() {
        val username = "mongo-user"
        val secret = "mongo-secret"
        val connectionString = "mongodb://$username:$secret@db.example.test/app"

        val configuration = DatabaseConfiguration.mongodb(
            connectionString = connectionString,
            database = "app"
        )

        assertRedacted(
            configuration.toString(),
            connectionString,
            username,
            secret
        )
    }

    @Test
    fun `Redis configuration redacts its connection string`() {
        val secret = "redis-secret"
        val connectionString = "redis://:$secret@db.example.test:6379/0"

        val configuration = DatabaseConfiguration.redis(
            connectionString = connectionString
        )

        assertRedacted(
            configuration.toString(),
            connectionString,
            secret
        )
    }

    private fun assertRedacted(
        representation: String,
        vararg secrets: String
    ) {
        secrets.forEach { secret ->
            assertFalse(
                representation.contains(secret),
                "Configuration representation exposed sensitive data."
            )
        }

        assertTrue(representation.contains("<redacted>"))
    }

}