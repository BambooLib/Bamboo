package io.github.deopping.bamboo.event.api

/**
 * Handles the exception thrown by event handlers.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public fun interface EventExceptionHandler {
    /**
     * Handles an exception thrown during event dispatch.
     *
     * @param event the event being dispatched
     * @param listener the listener whose handler failed
     * @param exception the thrown exception
     * @since 1.0.0
     */
    public fun onException(
        event: Event,
        listener: EventListener,
        exception: Throwable,
    )
}
