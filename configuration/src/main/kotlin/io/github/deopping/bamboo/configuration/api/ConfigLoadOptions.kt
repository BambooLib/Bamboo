package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema

/**
 * Defines optional behavior used when loading a configuration.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigLoadOptions {

    /**
     * Optional resource used to initialize the configuration file when it
     * does not already exist.
     */
    var resource: ConfigResource?

    /**
     * Optional schema used to validate and update the configuration.
     */
    var schema: ConfigSchema?

}
