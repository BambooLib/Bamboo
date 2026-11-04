package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.ConfigApi
import io.github.deopping.bamboo.configuration.api.ConfigLoadOptions
import io.github.deopping.bamboo.configuration.api.ConfigManager
import io.github.deopping.bamboo.configuration.api.ConfigResource
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackendFactory
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchemaBuilder
import io.github.deopping.bamboo.configuration.api.schema.ConfigSettingBuilder
import io.github.deopping.bamboo.configuration.internal.backends.DefaultConfigBackendFactory
import io.github.deopping.bamboo.configuration.internal.schema.DefaultConfigSchemaBuilder
import io.github.deopping.bamboo.configuration.internal.schema.DefaultConfigSettingBuilder
import java.nio.file.Path
import java.util.Optional

internal class DefaultConfigApi : ConfigApi {
    override fun createManager(
        parentPath: Optional<Path>,
        backendFactory: Optional<ConfigBackendFactory>,
    ): ConfigManager =
        DefaultConfigManager(
            parentPath = parentPath,
            backendFactory =
                backendFactory.orElseGet {
                    DefaultConfigBackendFactory()
                },
        )

    override fun createLoadOptions(
        resource: Optional<ConfigResource>,
        schema: Optional<ConfigSchema>,
    ): ConfigLoadOptions = DefaultConfigLoadOptions(resource, schema)

    override fun createConfigSchema(): ConfigSchemaBuilder = DefaultConfigSchemaBuilder()

    override fun <T : Any> createConfigSetting(
        path: String,
        defaultValue: T,
    ): ConfigSettingBuilder<T> {
        return DefaultConfigSettingBuilder(path, defaultValue)
    }
}
