package io.github.deopping.bamboo.configuration.internal.backends

import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackend
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackendFactory
import io.github.deopping.bamboo.configuration.internal.backends.impl.NightConfigBackend
import java.nio.file.Path

internal class DefaultConfigBackendFactory : ConfigBackendFactory {
    override fun create(
        path: Path,
        format: ConfigFormat,
    ): ConfigBackend = NightConfigBackend(path, format)
}
