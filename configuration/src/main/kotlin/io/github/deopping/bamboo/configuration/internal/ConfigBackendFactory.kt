package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.internal.backends.NightConfigBackend
import java.nio.file.Path

internal object ConfigBackendFactory {

    fun create(path: Path, format: ConfigFormat): ConfigBackend {
        return when (format) {
            ConfigFormat.YAML,
            ConfigFormat.JSON,
            ConfigFormat.HOCON,
            ConfigFormat.TOML -> NightConfigBackend(path, format)
        }
    }

}