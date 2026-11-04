package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.internal.schema.DefaultConfigSettingBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Optional

private class DefaultConfigSettingTest {
    @Test
    fun `stores its path and default value`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.max-stack",
                defaultValue = 64,
            ).build()

        assertEquals("cards.max-stack", setting.path)
        assertEquals(64, setting.defaultValue)
    }

    @Test
    fun `defaults to no comment`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.enabled",
                defaultValue = true,
            ).build()

        assertEquals(
            Optional.empty<String>(),
            setting.comment,
        )
    }

    @Test
    fun `preserves a configured comment`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.enabled",
                defaultValue = true,
            )
                .comment("Controls card drops")
                .build()

        assertEquals(
            Optional.of("Controls card drops"),
            setting.comment,
        )
    }

    @Test
    fun `accepts a value that passes validation`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.max-stack",
                defaultValue = 64,
            )
                .validator { value ->
                    value in 1..99
                }.build()

        assertTrue(setting.isValid(32))
    }

    @Test
    fun `rejects a value that fails validation`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.max-stack",
                defaultValue = 64,
            )
                .validator { value ->
                    value in 1..99
                }.build()

        assertFalse(setting.isValid(100))
    }

    @Test
    fun `returns false when a value has the wrong type`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.max-stack",
                defaultValue = 64,
            )
                .validator { value ->
                    value in 1..99
                }.build()

        assertFalse(setting.isValid("not-an-integer"))
    }

    @Test
    fun `accepts values when no additional validator is configured`() {
        val setting =
            DefaultConfigSettingBuilder(
                path = "cards.enabled",
                defaultValue = true,
            ).build()

        assertTrue(setting.isValid(true))
    }

    @Test
    fun `rejects a blank path`() {
        assertThrows(IllegalArgumentException::class.java) {
            DefaultConfigSettingBuilder(
                path = " ",
                defaultValue = true,
            ).build()
        }
    }

    @Test
    fun `rejects a blank comment`() {
        assertThrows(IllegalArgumentException::class.java) {
            DefaultConfigSettingBuilder(
                path = "cards.enabled",
                defaultValue = true,
            ).comment(" ")
        }
    }
}
