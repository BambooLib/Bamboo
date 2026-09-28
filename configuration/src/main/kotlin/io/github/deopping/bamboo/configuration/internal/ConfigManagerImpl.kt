package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.ConfigLoadOptions
import io.github.deopping.bamboo.configuration.api.ConfigManager
import io.github.deopping.bamboo.configuration.api.ConfigResource
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

internal class ConfigManagerImpl(
    parentPath: Path? = null
) : ConfigManager {

    private val parentPath = parentPath
        ?.toAbsolutePath()
        ?.normalize()

    private val configs = ConcurrentHashMap<Path, Config>()

    override fun load(path: Path): Config {
        return load(
            path = path,
            options = ConfigLoadOptionsImpl()
        )
    }

    override fun load(
        path: Path,
        options: ConfigLoadOptions
    ): Config {
        val normalizedPath = normalize(path)

        configs[normalizedPath]?.let {
            return it
        }

        val existing = Files.exists(normalizedPath)

        if (!existing) {
            initializeFile(
                path = normalizedPath,
                resource = options.resource
            )
        }

        val format = ConfigFormat.detect(normalizedPath)

        val backend = ConfigBackendFactory.create(
            normalizedPath,
            format
        )

        val config = ConfigImpl(
            path = normalizedPath,
            format = format,
            backend = backend
        )

        options.schema?.let { schema ->
            ConfigUpdater.update(
                config = config,
                schema = schema,
                newlyCreated = !existing
            )
        }

        val managed = configs.putIfAbsent(
            normalizedPath,
            config
        )

        if (managed != null) {
            config.close()
            return managed
        }

        return config
    }

    override fun unload(path: Path) {
        configs.remove(normalize(path))?.close()
    }

    override fun get(path: Path): Config? {
        return configs[normalize(path)]
    }

    override fun saveAll() {
        configs.values.forEach(Config::save)
    }

    override fun reloadChanged(): Int {
        var reloaded = 0

        configs.values.forEach { config ->
            if ((config as? ManagedConfig)?.reloadIfChanged() == true) {
                reloaded++
            }
        }

        return reloaded
    }

    override fun close() {
        configs.values.forEach(Config::close)
        configs.clear()
    }

    private fun initializeFile(
        path: Path,
        resource: ConfigResource?
    ) {
        path.parent?.let(Files::createDirectories)

        if (resource == null) {
            Files.createFile(path)
            return
        }

        resource.open().use { input ->
            Files.copy(input, path)
        }
    }

    private fun normalize(path: Path): Path {
        val resolved =
            if (parentPath != null && !path.isAbsolute) {
                parentPath.resolve(path)
            }
            else path

        return resolved
            .toAbsolutePath()
            .normalize()
    }

}