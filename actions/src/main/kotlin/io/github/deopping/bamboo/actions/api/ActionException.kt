package io.github.deopping.bamboo.actions.api

/**
 * Represents an error encountered while working with actions.
 *
 * @author DeOpping
 * @since 0.1.0
 */
class ActionException : RuntimeException {

    /**
     * Creates an action exception with a message and underlying cause
     *
     * @param message the description of the failure
     * @param throwable underlying cause
     * @since 0.1.0
     */
    constructor(message: String, throwable: Throwable?) : super(message, throwable)

    /**
     * Creates an action exception with a message.
     *
     * @param message the description of the failure
     * @since 0.1.0
     */
    constructor(message: String) : super(message)

    /**
     * Creates an action exception from an underlying cause
     *
     * @param throwable underlying cause
     * @since 0.1.0
     */
    constructor(throwable: Throwable) : super(throwable)

}