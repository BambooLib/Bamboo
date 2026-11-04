package io.github.deopping.bamboo.configuration

import io.github.deopping.bamboo.configuration.api.ConfigLoadOptions
import io.github.deopping.bamboo.configuration.api.ConfigManager
import io.github.deopping.bamboo.configuration.api.ConfigResource
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackendFactory
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchemaBuilder
import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigSettingBuilder
import io.github.deopping.bamboo.configuration.internal.DefaultConfigApi
import java.nio.file.Path
import java.util.Optional

/**
 * Provides access to Bamboo's configuration system.
 *
 * This API serves as the entry point for creating configuration managers,
 * configuring configuration loading behavior, and constructing configuration
 * schemas and settings.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigApi {
    /**
     * Creates a configuration manager.
     *
     * The manager is responsible for loading, tracking, saving, reloading,
     * and closing configurations.
     *
     * Relative configuration paths are resolved against [parentPath] when
     * a parent directory is provided. Absolute paths are used independently
     * of the configured parent directory.
     *
     * @param parentPath the optional parent directory for relative configuration paths
     * @param backendFactory the optional factory used to create configuration backends;
     * the default backend factory is used when absent.
     * @return a new configuration manager
     * @since 1.0.0
     */
    public fun createManager(
        parentPath: Optional<Path> = Optional.empty(),
        backendFactory: Optional<ConfigBackendFactory> = Optional.empty(),
    ): ConfigManager

    /**
     * Creates configuration loading options.
     *
     * Loading options control optional resource initialization and schema
     * application when a configuration is loaded.
     *
     * A resource is used to initialize a configuration file when the target
     * file does not already exist. A schema, when provided, is applied during
     * configuration loading.
     *
     * @param resource the optional resource used to initialize a new configuration file
     * @param schema the optional schema used to configure and update the loaded configuration
     * @return the configuration loading options
     * @since 1.0.0
     */
    public fun createLoadOptions(
        resource: Optional<ConfigResource> = Optional.empty(),
        schema: Optional<ConfigSchema> = Optional.empty(),
    ): ConfigLoadOptions

    /**
     * Creates a configuration schema builder.
     *
     * The returned builder can be used to define configuration settings,
     * versioning, migrations, and comments.
     *
     * @return a new configuration schema builder
     * @since 1.0.0
     */
    public fun createConfigSchema(): ConfigSchemaBuilder

    /**
     * Creates a configuration setting builder.
     *
     * The returned builder can be used to configure the setting's comment
     * and validation rules before constructing the setting.
     *
     * @param path the configuration path associated with the setting
     * @param defaultValue the default value assigned when the setting is missing or invalid
     *
     * @return a new configuration setting builder
     * @since 1.0.0
     */
    public fun <T : Any> createConfigSetting(
        path: String,
        defaultValue: T,
    ): ConfigSettingBuilder<T>

    /**
     * Provides access to the default configuration API instance.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Returns the default configuration API instance.
         *
         * @return the configuration API instance
         * @since 1.0.0
         */
        @JvmStatic
        public fun get(): ConfigApi {
            return DefaultConfigApi()
        }
    }
}

/**
 * Creates and builds a configuration schema using a Kotlin DSL.
 *
 * The supplied [block] is executed against a newly created schema builder.
 * The schema is then constructed and returned.
 *
 * @param block the configuration schema definition
 * @return the constructed configuration schema
 * @since 1.0.0
 */
public fun ConfigApi.createConfigSchema(block: ConfigSchemaBuilder.() -> Unit): ConfigSchema {
    return createConfigSchema()
        .apply(block)
        .build()
}

/**
 * Creates and builds a configuration setting using a Kotlin DSL.
 *
 * The supplied [block] is executed against a newly created setting builder.
 * The setting is then constructed and returned.
 *
 * @param T the type of the setting's default value
 * @param path the configuration path associated with the setting
 * @param defaultValue the default value assigned when the setting is missing or invalid
 * @param block the configuration setting definition
 * @return the constructed configuration setting
 * @since 1.0.0
 */
public fun <T : Any> ConfigApi.createConfigSetting(
    path: String,
    defaultValue: T,
    block: ConfigSettingBuilder<T>.() -> Unit,
): ConfigSetting<T> {
    return createConfigSetting(path, defaultValue)
        .apply(block)
        .build()
}
