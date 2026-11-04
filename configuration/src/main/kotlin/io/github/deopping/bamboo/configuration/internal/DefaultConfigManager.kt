package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.ConfigLoadOptions
import io.github.deopping.bamboo.configuration.api.ConfigManager
import io.github.deopping.bamboo.configuration.api.ConfigResource
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackendFactory
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.internal.backends.DefaultConfigBackendFactory
import io.github.deopping.bamboo.configuration.internal.updater.ConfigUpdater
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class DefaultConfigManager(
    parentPath: Optional<Path>,
    private val backendFactory: ConfigBackendFactory = DefaultConfigBackendFactory(),
) : ConfigManager {
    private val parentPath =
        if (parentPath.isEmpty) {
            Optional.empty<Path>()
        } else {
            Optional.of(parentPath.get().toAbsolutePath().normalize())
        }

    private val lock = ReentrantReadWriteLock()
    private val configs = ConcurrentHashMap<Path, Config>()

    internal var closed = false
        private set

    override fun load(path: Path): Config = load(path, DefaultConfigLoadOptions())

    override fun load(
        path: Path,
        options: ConfigLoadOptions,
    ): Config {
        val normalizedPath = normalizePath(path)

        return lock.write {
            ensureOpen()

            configs[normalizedPath]?.let {
                return@write it
            }

            loadInternal(normalizedPath, options)
        }
    }

    override fun loadAsync(
        path: Path,
        executor: Executor,
    ): CompletableFuture<Config> =
        CompletableFuture.supplyAsync(
            { load(path) },
            executor,
        )

    override fun loadAsync(
        path: Path,
        options: ConfigLoadOptions,
        executor: Executor,
    ): CompletableFuture<Config> =
        CompletableFuture.supplyAsync(
            { load(path, options) },
            executor,
        )

    override fun find(path: Path): Optional<Config> =
        lock.read {
            ensureOpen()
            Optional.ofNullable(configs[normalizePath(path)])
        }

    override fun unload(path: Path) {
        val config =
            lock.write {
                ensureOpen()
                configs.remove(normalizePath(path))
            } ?: return

        config.close()
    }

    override fun saveAll() {
        lock.read {
            ensureOpen()

            runForAll(
                operation = "save configurations",
                action = Config::save,
            )
        }
    }

    override fun saveAllAsync(executor: Executor): CompletableFuture<Void> =
        CompletableFuture.runAsync(
            { saveAll() },
            executor,
        )

    override fun reloadChanged(): Int =
        lock.read {
            ensureOpen()

            var reloaded = 0
            val failures = mutableListOf<Exception>()

            for (config in configs.values) {
                try {
                    if (config.reloadIfChanged()) {
                        reloaded++
                    }
                } catch (exception: Exception) {
                    failures.add(exception)
                }
            }

            throwIfFailures(
                operation = "reload changed configurations",
                failures = failures,
            )

            reloaded
        }

    override fun reloadChangedAsync(executor: Executor): CompletableFuture<Int> =
        CompletableFuture.supplyAsync(
            { reloadChanged() },
            executor,
        )

    override fun close() {
        val toClose =
            lock.write {
                if (closed) {
                    return
                }

                closed = true

                configs.values.toList().also {
                    configs.clear()
                }
            }

        val failures = mutableListOf<Exception>()

        for (config in toClose) {
            try {
                config.close()
            } catch (exception: Exception) {
                failures.add(exception)
            }
        }

        throwIfFailures(
            operation = "close configurations",
            failures,
        )
    }

    private fun loadInternal(
        path: Path,
        options: ConfigLoadOptions,
    ): Config {
        try {
            val existed = Files.exists(path)

            if (!existed) {
                initializeFile(
                    path = path,
                    resource = options.resource.orElse(null),
                )
            }

            val format = ConfigFormat.detect(path)
            val backend = backendFactory.create(path, format)
            val config = DefaultConfig(path, format, backend)

            try {
                options.schema.ifPresent { schema ->
                    ConfigUpdater.update(
                        config = config,
                        schema = schema,
                        newlyCreated = !existed,
                    )
                }
            } catch (exception: Exception) {
                try {
                    config.reload(discardUnsavedChanges = true)
                } catch (restoreException: Exception) {
                    exception.addSuppressed(restoreException)
                }

                try {
                    config.close()
                } catch (closeException: Exception) {
                    exception.addSuppressed(closeException)
                }

                throw exception
            }

            configs[path] = config
            return config
        } catch (exception: Exception) {
            throw ConfigException(
                "Failed to load configuration '$path'.",
                exception,
            )
        }
    }

    private fun initializeFile(
        path: Path,
        resource: ConfigResource?,
    ) {
        path.parent?.let(Files::createDirectories)

        if (Files.exists(path)) {
            return
        }

        if (resource == null) {
            Files.createFile(path)
            return
        }

        resource.open().use { input ->
            Files.copy(input, path)
        }
    }

    private fun runForAll(
        operation: String,
        action: (Config) -> Unit,
    ) {
        val failures = mutableListOf<Exception>()

        for (config in configs.values) {
            try {
                action(config)
            } catch (exception: Exception) {
                failures.add(exception)
            }
        }

        throwIfFailures(operation, failures)
    }

    private fun throwIfFailures(
        operation: String,
        failures: List<Exception>,
    ) {
        if (failures.isEmpty()) {
            return
        }

        val exception =
            ConfigException(
                "Failed to $operation: ${failures.size} operation(s) failed.",
                failures.first(),
            )

        failures.drop(1).forEach(exception::addSuppressed)

        throw exception
    }

    private fun ensureOpen() {
        if (closed) {
            throw ConfigException("Configuration manager is closed.")
        }
    }

    private fun normalizePath(path: Path): Path {
        val resolved =
            if (parentPath.isPresent && !path.isAbsolute) {
                parentPath.get().resolve(path)
            } else {
                path
            }

        return resolved.toAbsolutePath().normalize()
    }
}
