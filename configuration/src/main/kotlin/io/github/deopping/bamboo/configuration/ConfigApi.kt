package io.github.deopping.bamboo.configuration

import io.github.deopping.bamboo.configuration.api.ConfigManager
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchemaBuilder
import java.nio.file.Path

/**
 * Entry point for Bamboo's configuration system.
 *
 * A `ConfigApi` creates independent [ConfigManager] instances
 * that manage the lifecycle of configuration files. A manager may optionally
 * be given a parent directory, allowing configuration paths to be specified
 * relative to that directory.
 *
 * The API itself is platform-independent. Platform integrations are
 * responsible for providing the implementation of this interface.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigApi {

    /**
     * Creates a new configuration manager.
     *
     * When a parent path is supplied, relative paths passed to the
     * resulting manager are resolved against that directory. Absolute paths
     * remain absolute.
     *
     * The returned manager owns the configuration loaded through it and
     * should be closed when it is no longer needed.
     *
     * @param parentPath optional base directory for relative configuration paths
     * @return a new [ConfigManager]
     * @since 0.1.0
     */
    fun createConfigManager(parentPath: Path? = null): ConfigManager

    /**
     * Creates a new configuration schema builder.
     *
     * The returned builder is independent of any configuration manager and
     * can be used to define settings, validation rules, and migrations before
     * producing a [ConfigSchema].
     *
     * @return a new [ConfigSchemaBuilder]
     * @since 0.1.0
     */
    fun createConfigSchemaBuilder(): ConfigSchemaBuilder

}