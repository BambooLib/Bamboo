package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackendFactory
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.internal.backends.InMemoryConfigBackend
import io.github.deopping.bamboo.configuration.internal.schema.DefaultConfigSchemaBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private class DefaultConfigManagerTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `loading a path twice returns the same configuration`() {
        val backends = backends()

        createManager(backends).use { manager ->
            val first = manager.load(Path.of("example.toml"))
            val second = manager.load(Path.of("example.toml"))

            assertSame(first, second)
            assertEquals(1, backends.size)
            assertEquals(1, backends.values.single().loadCount.get())
        }
    }

    @Test
    fun `loading equivalent normalized paths returns the same configuration`() {
        val backends = backends()

        createManager(backends).use { manager ->
            val first = manager.load(Path.of("example.toml"))
            val second = manager.load(Path.of("subdirectory", "..", "example.toml"))

            assertSame(first, second)
            assertEquals(1, backends.size)
            assertEquals(1, backends.values.single().loadCount.get())
        }
    }

    @Test
    fun `failed backend loading closes the backend`() {
        lateinit var backend: InMemoryConfigBackend

        val factory =
            ConfigBackendFactory { path, format ->
                InMemoryConfigBackend(path, format).also {
                    backend = it
                    it.loadFailure = IllegalStateException("Simulated load failure")
                }
            }

        DefaultConfigManager(
            parentPath = Optional.of(tempDir),
            backendFactory = factory,
        ).use { manager ->
            assertThrows(ConfigException::class.java) {
                manager.load(Path.of("broken.toml"))
            }

            assertTrue(backend.closed)
            assertEquals(1, backend.closeCount.get())
            assertTrue(manager.find(Path.of("broken.toml")).isEmpty)
        }
    }

    @Test
    fun `schema defaults are applied when loading`() {
        val backends = backends()
        val manager = createManager(backends)

        val schema =
            DefaultConfigSchemaBuilder().apply {
                setting("example.enabled", true)
                setting("example.max-stack", 64)
            }.build()

        val options =
            DefaultConfigLoadOptions(
                schema = Optional.of(schema),
            )

        manager.use { manager ->
            val config =
                manager.load(
                    Path.of("example.toml"),
                    options,
                )

            assertEquals(
                true,
                config.getBoolean("example.enabled").orElseThrow(),
            )

            assertEquals(
                64,
                config.getInt("example.max-stack").orElseThrow(),
            )
        }
    }

    @Test
    fun `failed schema loading closes the newly created configuration`() {
        val file = tempDir.resolve("existing.toml")
        Files.writeString(file, "")

        lateinit var backend: InMemoryConfigBackend

        val factory =
            ConfigBackendFactory { path, format ->
                InMemoryConfigBackend(
                    path = path,
                    format = format,
                    initialValues =
                        mapOf(
                            listOf("schema-version") to 0,
                        ),
                ).also {
                    backend = it
                }
            }

        val manager =
            DefaultConfigManager(
                parentPath = Optional.of(tempDir),
                backendFactory = factory,
            )

        val schema =
            DefaultConfigSchemaBuilder().apply {
                version(2, "schema-version")
            }.build()

        manager.use {
            assertThrows(ConfigException::class.java) {
                manager.load(
                    Path.of("existing.toml"),
                    DefaultConfigLoadOptions(schema = Optional.of(schema)),
                )
            }

            assertTrue(backend.closed)
            assertEquals(1, backend.closeCount.get())
        }
    }

    @Test
    fun `save all continues after a configuration fails to save`() {
        val backends = backends()

        createManager(backends).use { manager ->
            val first = manager.load(Path.of("first.toml"))
            val second = manager.load(Path.of("second.toml"))

            val firstBackend = backends[first.path]!!
            val secondBackend = backends[second.path]!!

            first.set("value", 1)
            second.set("value", 2)

            firstBackend.saveFailure = IllegalStateException("Simulated save failure")

            assertThrows(ConfigException::class.java) {
                manager.saveAll()
            }

            assertEquals(1, firstBackend.saveCount.get())
            assertEquals(1, secondBackend.saveCount.get())

            firstBackend.saveFailure = null
        }
    }

    @Test
    fun `closing the manager closes every configuration despite failures`() {
        val backends = backends()
        val manager = createManager(backends)

        manager.load(Path.of("first.toml"))
        manager.load(Path.of("second.toml"))

        val firstBackend = backends.values.first()
        val secondBackend = backends.values.last()

        firstBackend.closeFailure = IllegalStateException("Simulated close failure")

        assertThrows(ConfigException::class.java) {
            manager.close()
        }

        assertTrue(firstBackend.closed)
        assertTrue(secondBackend.closed)
        assertEquals(1, firstBackend.closeCount.get())
        assertEquals(1, secondBackend.closeCount.get())

        manager.close()
    }

    @Test
    fun `closed manager rejects further operations`() {
        val manager = createManager(linkedMapOf())
        manager.close()

        assertThrows(ConfigException::class.java) {
            manager.load(Path.of("example.toml"))
        }

        assertThrows(ConfigException::class.java) {
            manager.find(Path.of("example.toml"))
        }

        assertThrows(ConfigException::class.java) {
            manager.saveAll()
        }
    }

    @Test
    fun `concurrent loads create only one managed configuration`() {
        val creationCount = AtomicInteger()

        val factory =
            ConfigBackendFactory { path, format ->
                creationCount.incrementAndGet()
                InMemoryConfigBackend(path, format)
            }

        val manager =
            DefaultConfigManager(
                parentPath = Optional.of(tempDir),
                backendFactory = factory,
            )

        val threadCount = 8
        val executor = Executors.newFixedThreadPool(threadCount)
        val ready = CountDownLatch(threadCount)
        val start = CountDownLatch(1)

        try {
            val tasks =
                (1..threadCount).map {
                    executor.submit<Config> {
                        ready.countDown()

                        check(start.await(5, TimeUnit.SECONDS)) {
                            "Timed out waiting for concurrent load start."
                        }

                        manager.load(Path.of("shared.toml"))
                    }
                }

            assertTrue(ready.await(5, TimeUnit.SECONDS))
            start.countDown()

            val results =
                tasks.map {
                    it.get(5, TimeUnit.SECONDS)
                }

            results.forEach { config ->
                assertSame(results.first(), config)
            }

            assertEquals(1, creationCount.get())
        } finally {
            start.countDown()
            executor.shutdownNow()
            manager.close()
        }
    }

    private fun backends() = linkedMapOf<Path, InMemoryConfigBackend>()

    private fun createManager(backends: MutableMap<Path, InMemoryConfigBackend>): DefaultConfigManager {
        val factory =
            ConfigBackendFactory { path, format ->
                InMemoryConfigBackend(path, format).also {
                    backends[path] = it
                }
            }

        return DefaultConfigManager(
            parentPath = Optional.of(tempDir),
            backendFactory = factory,
        )
    }
}
