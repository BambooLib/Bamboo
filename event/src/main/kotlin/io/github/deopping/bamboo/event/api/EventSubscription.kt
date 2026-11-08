package io.github.deopping.bamboo.event.api

import java.lang.AutoCloseable

/**
 * Represents a registered event listener.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface EventSubscription : AutoCloseable {
    /**
     * Whether this subscription is currently registered.
     *
     * @since 1.0.0
     */
    public val isRegistered: Boolean

    /**
     * Unregisters this subscription.
     *
     * This does not interrupt event dispatches that have already started.
     *
     * @return `true` if this subscription was unregistered, or
     * `false` if it was already unregistered
     * @since 1.0.0
     */
    public fun unregister(): Boolean

    /**
     * Unregisters this subscription.
     *
     * @since 1.0.0
     */
    public override fun close()
}
