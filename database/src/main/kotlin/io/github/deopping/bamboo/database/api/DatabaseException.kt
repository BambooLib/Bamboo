package io.github.deopping.bamboo.database.api

/**
 * Reports a database operation failure without exposing raw driver
 * exceptions messages or credential-bearing connection strings.
 *
 * Causes are represented by sanitized diagnostic snapshots. The original
 * driver exception is deliberately not retained as the cause.
 *
 * @author DeOpping
 * @since 0.1.0
 */
class DatabaseException : RuntimeException {

    /**
     * Creates a database exception with a message and underlying cause.
     *
     * @param message description of the failure
     * @param throwable underlying cause
     * @since 0.1.0
     */
    constructor(message: String, throwable: Throwable?) : super(message, throwable)

    /**
     * Creates a database exception with a message.
     *
     * @param message description of the failure
     * @since 0.1.0
     */
    constructor(message: String) : super(message)

    /**
     * Creates a database exception from an underlying cause.
     *
     * @param throwable underlying cause
     * @since 0.1.0
     */
    constructor(throwable: Throwable) : super(throwable)

}