package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigValueException
import io.github.deopping.bamboo.configuration.internal.backends.InMemoryConfigBackend
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

private class DefaultConfigTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `missing values return an empty Optional`() {
        val config = createConfig()

        assertFalse(config.getString("missing").isPresent)

        config.close()
    }

    @Test
    fun `incorrect types throw a configuration value exception`() {
        val backend = createBackend()

        backend.set(listOf("count"), "not-a-number")

        val config = createConfig(backend)

        assertThrows(ConfigValueException::class.java) {
            config.getInt("count")
        }

        config.close()
    }

    @Test
    fun `setting a changed value marks the config as dirty`() {
        val backend = createBackend()
        val config = createConfig(backend)

        config.set("count", 10)

        assertTrue(config.isDirty)

        config.close()
        assertEquals(1, backend.saveCount.get())
        assertTrue(backend.closed)
    }

    @Test
    fun `saving a clean config does not write again`() {
        val backend = createBackend()
        val config = createConfig(backend)

        config.save()

        assertEquals(0, backend.saveCount.get())

        config.close()
    }

    @Test
    fun `a closed config rejects subsequent reads`() {
        val config = createConfig()

        config.close()

        assertThrows(ConfigException::class.java) {
            config.get("value")
        }
    }

    private fun createBackend(): InMemoryConfigBackend {
        return InMemoryConfigBackend(
            path = tempDir.resolve("config.toml"),
            format = ConfigFormat.TOML,
        )
    }

    private fun createConfig(): Config {
        return createConfig(createBackend())
    }

    private fun createConfig(backend: InMemoryConfigBackend): Config {
        return DefaultConfig(
            path = backend.path,
            format = ConfigFormat.TOML,
            backend = backend,
        )
    }
}
