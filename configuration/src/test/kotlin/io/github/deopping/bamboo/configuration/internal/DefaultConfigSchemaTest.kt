package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersionStore
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersioning
import io.github.deopping.bamboo.configuration.internal.schema.DefaultConfigSchemaBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.Optional

private class DefaultConfigSchemaTest {
    @Test
    fun `builds a schema with settings and defaults`() {
        val schema =
            DefaultConfigSchemaBuilder().apply {
                setting("cards.enabled", true)
                setting("cards.max-stack", 64)
            }.build()

        assertEquals(2, schema.settings.size)
        assertEquals(true, schema.settings["cards.enabled"]?.defaultValue)
        assertEquals(64, schema.settings["cards.max-stack"]?.defaultValue)
    }

    @Test
    fun `builds embedded versioning`() {
        val schema =
            DefaultConfigSchemaBuilder().apply {
                version(2, "schema-version")
            }.build()

        assertTrue(schema.versioning.isPresent)

        val versioning = schema.versioning.orElseThrow()
        assertTrue(versioning is ConfigVersioning.Embedded)

        val embedded = versioning as ConfigVersioning.Embedded

        assertEquals(2, embedded.version)
        assertEquals("schema-version", embedded.path)
    }

    @Test
    fun `builds external versioning`() {
        val store = TestVersionStore()
        val schema =
            DefaultConfigSchemaBuilder().apply {
                externalVersion(3, store)
            }.build()

        val versioning = schema.versioning.orElseThrow()

        assertTrue(versioning is ConfigVersioning.External)

        val external = versioning as ConfigVersioning.External

        assertEquals(3, external.version)
        assertEquals(store, external.store)
    }

    @Test
    fun `builds sequential migrations`() {
        val schema =
            DefaultConfigSchemaBuilder().apply {
                version(2, "schema-version")
                migration(0, 1) {}
                migration(1, 2) {}
            }.build()

        assertEquals(listOf(0, 1), schema.migrations.keys.toList())
        assertEquals(1, schema.migrations[0]?.to)
        assertEquals(2, schema.migrations[1]?.to)
    }

    @Test
    fun `rejects a negative embedded version`() {
        val builder = DefaultConfigSchemaBuilder()

        assertThrows(IllegalArgumentException::class.java) {
            builder.version(-1, "schema-version")
        }
    }

    @Test
    fun `rejects a blank embedded version path`() {
        val builder = DefaultConfigSchemaBuilder()

        assertThrows(IllegalArgumentException::class.java) {
            builder.version(1, " ")
        }
    }

    @Test
    fun `rejects a blank setting path`() {
        val builder = DefaultConfigSchemaBuilder()

        assertThrows(IllegalArgumentException::class.java) {
            builder.setting(" ", true)
        }
    }

    @Test
    fun `rejects duplicate setting paths`() {
        val builder =
            DefaultConfigSchemaBuilder().apply {
                setting("cards.enabled", true)
            }

        assertThrows(IllegalArgumentException::class.java) {
            builder.setting("cards.enabled", false)
        }
    }

    @Test
    fun `rejects migrations without versioning`() {
        val builder =
            DefaultConfigSchemaBuilder().apply {
                migration(0, 1) {}
            }

        assertThrows(IllegalArgumentException::class.java) {
            builder.build()
        }
    }

    @Test
    fun `rejects duplicate migration sources`() {
        val builder =
            DefaultConfigSchemaBuilder().apply {
                version(2, "schema-version")
                migration(0, 1) {}
            }

        assertThrows(IllegalArgumentException::class.java) {
            builder.migration(0, 2) {}
        }
    }

    @Test
    fun `rejects a migration that does not advance`() {
        val builder =
            DefaultConfigSchemaBuilder().apply {
                version(2, "schema-version")
            }

        assertThrows(IllegalArgumentException::class.java) {
            builder.migration(1, 1) {}
        }
    }

    @Test
    fun `rejects a migration beyond the schema version`() {
        val builder =
            DefaultConfigSchemaBuilder().apply {
                version(2, "schema-version")
                migration(0, 3) {}
            }

        assertThrows(IllegalArgumentException::class.java) {
            builder.build()
        }
    }

    @Test
    fun `preserves root comment and anchor`() {
        val schema =
            DefaultConfigSchemaBuilder().apply {
                rootComment(
                    anchorPath = "cards.enabled",
                    comment = "Controls whether cards can drop.",
                )
            }.build()

        assertEquals(
            Optional.of("Controls whether cards can drop."),
            schema.rootComment,
        )

        assertEquals(
            Optional.of("cards.enabled"),
            schema.rootCommentAnchor,
        )
    }

    @Test
    fun `rejects blank root comments`() {
        val builder = DefaultConfigSchemaBuilder()

        assertThrows(IllegalArgumentException::class.java) {
            builder.rootComment("cards.enabled", " ")
        }
    }

    @Test
    fun `built schema retains values after builder changes`() {
        val builder = DefaultConfigSchemaBuilder()
        val settingBuilder = builder.setting("cards.enabled", true)

        val schema = builder.build()

        settingBuilder.comment("Added after building")

        assertEquals(
            Optional.empty<String>(),
            schema.settings["cards.enabled"]?.comment,
        )
    }

    @Test
    fun `defaults to no versioning and no comments`() {
        val schema = DefaultConfigSchemaBuilder().build()

        assertTrue(schema.versioning.isEmpty)
        assertTrue(schema.rootComment.isEmpty)
        assertTrue(schema.rootCommentAnchor.isEmpty)
    }

    private class TestVersionStore : ConfigVersionStore {
        override fun getVersion(path: Path): Optional<Int> {
            return Optional.empty()
        }

        override fun setVersion(
            path: Path,
            version: Int,
        ) = Unit
    }
}
