package io.github.deopping.bamboo.actions.api

import java.lang.AutoCloseable

/**
 * Represents a registration in an action registry.
 *
 * Closing the registration removes it from its registry.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ActionRegistration : AutoCloseable {

    /**
     * The registered action type
     * @since 0.1.0
     */
    val type: ActionType

    /**
     * Indicates whether this registration is still active.
     * @since 0.1.0
     */
    val isRegistered: Boolean

    /**
     * Removes this registration.
     *
     * Repeated calls must not remove a subsequent registration
     * of the same action type.
     *
     * @since 0.1.0
     */
    fun unregister()

    /**
     * Removes this registration.
     *
     * @since 0.1.0
     */
    override fun close() {
        unregister()
    }

}