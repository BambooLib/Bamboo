package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.internal.backends.InMemoryConfigBackend
import io.github.deopping.bamboo.configuration.internal.schema.migration.DefaultConfigMigrationScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.Optional
import kotlin.jvm.java

private class DefaultConfigMigrationScopeTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `renames a value`() {
        val backend =
            createBackend().apply {
                put("old-name", "Stone")
            }

        createConfig(backend).use { config ->
            DefaultConfigMigrationScope(config).apply {
                rename("old-name", "card-name", false)
            }

            assertFalse(config.contains("old-name"))
            assertEquals(
                "Stone",
                config.getString("card-name").orElseThrow(),
            )
        }
    }

    @Test
    fun `copies a value without removing the source`() {
        val backend =
            createBackend().apply {
                put("source", "Stone")
            }

        createConfig(backend).use { config ->
            DefaultConfigMigrationScope(config).apply {
                copy("source", "destination", false)

                assertEquals(
                    "Stone",
                    config.getString("source").orElseThrow(),
                )

                assertEquals(
                    "Stone",
                    config.getString("destination").orElseThrow(),
                )
            }
        }
    }

    @Test
    fun `rejects rename when destination exists and overwrite is false`() {
        val backend =
            createBackend().apply {
                put("source", "Stone")
                put("destination", "Dirt")
            }

        createConfig(backend).use { config ->
            val scope = DefaultConfigMigrationScope(config)

            assertThrows(ConfigException::class.java) {
                scope.rename("source", "destination", false)
            }

            assertEquals(
                "Stone",
                config.getString("source").orElseThrow(),
            )

            assertEquals(
                "Dirt",
                config.getString("destination").orElseThrow(),
            )
        }
    }

    @Test
    fun `overwrites destination when rename permits it`() {
        val backend =
            createBackend().apply {
                put("source", "Stone")
                put("destination", "Dirt")
            }

        createConfig(backend).use { config ->
            DefaultConfigMigrationScope(config).apply {
                rename("source", "destination", true)

                assertFalse(config.contains("source"))
                assertEquals(
                    "Stone",
                    config.getString("destination").orElseThrow(),
                )
            }
        }
    }

    @Test
    fun `rejects copy when destination exists and overwrite is false`() {
        val backend =
            createBackend().apply {
                put("source", "Stone")
                put("destination", "Dirt")
            }

        createConfig(backend).use { config ->
            val scope = DefaultConfigMigrationScope(config)

            assertThrows(ConfigException::class.java) {
                scope.copy("source", "destination", false)
            }

            assertEquals(
                "Dirt",
                config.getString("destination").orElseThrow(),
            )
        }
    }

    @Test
    fun `overwrites destination when copy permits it`() {
        val backend =
            createBackend().apply {
                put("source", "Stone")
                put("destination", "Dirt")
            }

        createConfig(backend).use { config ->
            val scope =
                DefaultConfigMigrationScope(config).apply {
                    copy("source", "destination", true)
                }

            assertEquals(
                "Stone",
                config.getString("destination").orElseThrow(),
            )

            assertTrue(config.contains("source"))
        }
    }

    @Test
    fun `renaming a missing source does nothing`() {
        val backend = createBackend()

        createConfig(backend).use { config ->
            DefaultConfigMigrationScope(config).apply {
                rename("missing", "destination", false)
            }

            assertFalse(config.contains("destination"))
        }
    }

    @Test
    fun `copying a missing source does nothing`() {
        val backend = createBackend()

        createConfig(backend).use { config ->
            DefaultConfigMigrationScope(config).apply {
                copy("missing", "destination", false)
            }

            assertFalse(config.contains("destination"))
        }
    }

    @Test
    fun `rename preserves comments for comment supporting formats`() {
        val backend =
            createBackend().apply {
                put("source", "Stone")
                setComment(listOf("source"), "Original comment")
            }

        createConfig(backend).use { config ->
            DefaultConfigMigrationScope(config).apply {
                rename("source", "destination", false)
            }

            assertEquals(
                Optional.of("Original comment"),
                config.getComment("destination"),
            )
        }
    }

    private fun createBackend(): InMemoryConfigBackend {
        return InMemoryConfigBackend(
            path = tempDir.resolve("migration.toml"),
            format = ConfigFormat.TOML,
        )
    }

    private fun createConfig(backend: InMemoryConfigBackend): Config {
        return DefaultConfig(
            path = backend.path,
            format = backend.format,
            backend = backend,
        )
    }
}
