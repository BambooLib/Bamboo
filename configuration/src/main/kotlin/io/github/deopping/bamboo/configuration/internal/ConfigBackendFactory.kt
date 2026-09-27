package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.backends.ConfigurateBackend
import io.github.deopping.bamboo.configuration.backends.NightConfigTomlBackend
import java.nio.file.Path

internal object ConfigBackendFactory {

    fun create(path: Path, format: ConfigFormat): ConfigBackend {
        return when (format) {
            ConfigFormat.TOML -> NightConfigTomlBackend(path)

            ConfigFormat.YAML,
            ConfigFormat.JSON,
            ConfigFormat.HOCON -> ConfigurateBackend(path, format)
        }
    }

}