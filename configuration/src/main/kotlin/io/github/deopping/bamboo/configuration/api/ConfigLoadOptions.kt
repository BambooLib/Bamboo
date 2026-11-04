package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import java.util.Optional

/**
 * Defines the options used when loading a configuration.
 *
 * Loading options allow a configuration to be initialized from a resource
 * and updated according to a configuration schema.
 *
 * @property resource the optional resource used to initialize a new configuration file
 * @property schema the optional schema applied when loading the configuration
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigLoadOptions {
    /**
     * Gets the resource used to initialize a configuration file.
     *
     * The resource is only used when the target configuration file does
     * not already exist.
     *
     * @return the initialization resource, or an empty optional if none is configured
     * @since 1.0.0
     */
    public val resource: Optional<ConfigResource>

    /**
     * Gets the schema applied to the configuration.
     *
     * When present, the schema is used to apply default settings, comments,
     * versioning, and migrations during configuration loading.
     *
     * @return the configuration schema, or an empty optional if none is configured
     * @since 1.0.0
     */
    public val schema: Optional<ConfigSchema>
}
