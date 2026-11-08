package io.github.deopping.bamboo.event.api

import io.github.deopping.bamboo.event.internal.DefaultEventManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * Manages the event listener registration and event dispatch.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface EventManager {
    /**
     * Registers an event listener.
     *
     * The listener must contain at least one valid method annotated with
     * [EventHandler].
     *
     * @param listener the listener to register
     * @return a subscription representing the registration
     * @throws IllegalArgumentException if the listener contains invalid handlers
     * @throws IllegalStateException if the listener is already registered
     * @since 1.0.0
     */
    public fun register(listener: EventListener): EventSubscription

    /**
     * Dispatches an event synchronously on the calling thread.
     *
     * Handlers execute sequentially according to their priority.
     *
     * @param event the event to dispatch
     * @return the dispatched event
     * @since 1.0.0
     */
    public fun <T : Event> callEvent(event: T): T

    /**
     * Dispatches an event asynchronously using the configured executor.
     *
     * Handlers for this event still execute sequentially.
     *
     * @param event the event to dispatch
     * @return a future completed with the dispatched event
     * @since 1.0.0
     */
    public fun <T : Event> callEventAsync(event: T): CompletableFuture<T>

    public companion object {
        /**
         * Creates an event manager.
         *
         * @param executor the executor used for asynchronous event dispatch
         * @param exceptionHandler the handler used to report listener exceptions
         * @return a new event manager
         * @since 1.0.0
         */
        public fun create(
            executor: Executor,
            exceptionHandler: EventExceptionHandler,
        ): EventManager = DefaultEventManager(executor, exceptionHandler)
    }
}
