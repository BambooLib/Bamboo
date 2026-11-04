package io.github.deopping.bamboo.configuration.api.exceptions

/**
 * Represents an error encountered while working with a configuration.
 *
 * This exception serves as the base exception for configuration-related
 * failures, including loading, reading, writing, saving, reloading, and
 * updating configuration files.
 *
 * @param message a description of the configuration failure
 * @param cause the underlying cause of the failure, if available
 *
 * @author DeOpping
 * @since 1.0.0
 */
public open class ConfigException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
