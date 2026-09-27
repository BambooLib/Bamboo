package io.github.deopping.bamboo.configuration.api

/**
 * Thrown when a configuration operation cannot be completed.
 *
 * This exception is used for configuration-related failures such as
 * unsupported formats, file access errors, parsing failures, or serialization
 * failures.
 *
 * @author DeOpping
 * @since 0.1.0
 */
class ConfigException : RuntimeException {

    /**
     * Creates a configuration exception with a message and underlying cause.
     *
     * @param message description of the failure
     * @param throwable underlying cause
     * @since 0.1.0
     */
    constructor(message: String, throwable: Throwable?) : super(message, throwable)

    /**
     * Creates a configuration exception with a message.
     *
     * @param message description of the failure
     * @since 0.1.0
     */
    constructor(message: String) : super(message)

    /**
     * Creates a configuration exception from an underlying cause.
     *
     * @param throwable underlying cause
     * @since 0.1.0
     */
    constructor(throwable: Throwable) : super(throwable)

}