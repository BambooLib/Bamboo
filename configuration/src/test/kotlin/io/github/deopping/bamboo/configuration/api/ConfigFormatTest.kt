package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path

private class ConfigFormatTest {
    @Test
    fun `detects YAML format`() {
        assertEquals(
            ConfigFormat.YAML,
            ConfigFormat.detect(Path.of("example.yml")),
        )

        assertEquals(
            ConfigFormat.YAML,
            ConfigFormat.detect(Path.of("example.yaml")),
        )
    }

    @Test
    fun `detects JSON format`() {
        assertEquals(
            ConfigFormat.JSON,
            ConfigFormat.detect(Path.of("example.json")),
        )
    }

    @Test
    fun `detects HOCON format`() {
        assertEquals(
            ConfigFormat.HOCON,
            ConfigFormat.detect(Path.of("example.conf")),
        )

        assertEquals(
            ConfigFormat.HOCON,
            ConfigFormat.detect(Path.of("example.hocon")),
        )
    }

    @Test
    fun `detects TOML format`() {
        assertEquals(
            ConfigFormat.TOML,
            ConfigFormat.detect(Path.of("example.toml")),
        )
    }

    @Test
    fun `detects extensions without case sensitivity`() {
        assertEquals(
            ConfigFormat.YAML,
            ConfigFormat.detect(Path.of("EXAMPLE.YML")),
        )
        assertEquals(
            ConfigFormat.YAML,
            ConfigFormat.detect(Path.of("EXAMPLE.YAML")),
        )
        assertEquals(
            ConfigFormat.JSON,
            ConfigFormat.detect(Path.of("EXAMPLE.JSON")),
        )
        assertEquals(
            ConfigFormat.HOCON,
            ConfigFormat.detect(Path.of("EXAMPLE.CONF")),
        )
        assertEquals(
            ConfigFormat.HOCON,
            ConfigFormat.detect(Path.of("EXAMPLE.HOCON")),
        )
        assertEquals(
            ConfigFormat.TOML,
            ConfigFormat.detect(Path.of("EXAMPLE.TOML")),
        )
    }

    @Test
    fun `supports comments only for TOML and HOCON`() {
        assertFalse(ConfigFormat.YAML.supportsComments)
        assertFalse(ConfigFormat.JSON.supportsComments)
        assertTrue(ConfigFormat.HOCON.supportsComments)
        assertTrue(ConfigFormat.TOML.supportsComments)
    }

    @Test
    fun `exposes supported extensions`() {
        assertEquals(setOf("yml", "yaml"), ConfigFormat.YAML.extensions)
        assertEquals(setOf("json"), ConfigFormat.JSON.extensions)
        assertEquals(setOf("conf", "hocon"), ConfigFormat.HOCON.extensions)
        assertEquals(setOf("toml"), ConfigFormat.TOML.extensions)
    }

    @Test
    fun `rejects unsupported extensions`() {
        assertThrows(ConfigException::class.java) {
            ConfigFormat.detect(Path.of("example.bin"))
        }
    }

    @Test
    fun `rejects paths without an extension`() {
        assertThrows(ConfigException::class.java) {
            ConfigFormat.detect(Path.of("example"))
        }
    }
}
