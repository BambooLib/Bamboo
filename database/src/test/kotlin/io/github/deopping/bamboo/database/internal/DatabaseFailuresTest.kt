package io.github.deopping.bamboo.database.internal

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class DatabaseFailuresTest {

    @Test
    fun `sensitive assignments are redacted`() {
        val message = """
            password=secret123
            api_key=key123
            access-key=key456
            {"token": "token789"}
        """.trimIndent()

        val sanitized = DatabaseFailures.create(message).message!!

        listOf(
            "secret123",
            "key123",
            "key456",
            "token789"
        ).forEach { secret ->
            assertFalse(sanitized.contains(secret))
        }
    }
    
}