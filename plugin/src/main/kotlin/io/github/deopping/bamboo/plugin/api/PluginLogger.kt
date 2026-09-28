package io.github.deopping.bamboo.plugin.api

/**
 * Provides a logging functionality to a Bamboo plugin.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PluginLogger {

    /**
     * Logs an informational message.
     *
     * @param message message to log
     * @since 0.1.0
     */
    fun info(message: String)

    /**
     * Logs a warning message.
     *
     * @param message message to log
     * @since 0.1.0
     */
    fun warning(message: String)

    /**
     * Logs an error message.
     * @param message message to log
     * @since 0.1.0
     */
    fun error(message: String)

    /**
     * Logs an error message and associated exception.
     *
     * @param message message to log
     * @param throwable exception associated with the error
     * @since 0.1.0
     */
    fun error(
        message: String,
        throwable: Throwable
    )

}