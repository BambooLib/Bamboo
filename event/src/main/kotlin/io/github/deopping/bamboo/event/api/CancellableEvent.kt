package io.github.deopping.bamboo.event.api

/**
 * Represents an event whose execution can be canceled.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface CancellableEvent : Event {
    /**
     * Whether this event has been canceled.
     *
     * @since 1.0.0
     */
    public var isCanceled: Boolean
}
