package io.github.deopping.bamboo.packets.api

import java.lang.AutoCloseable

/**
 * Represents a registered packet listener.
 *
 * Closing the registration unregisters its listener handlers. Closing an
 * already closed registration has no effect.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketRegistration : AutoCloseable {

    /**
     * Gets whether this registration is currently active.
     *
     * @return `true` if the registration is active, `false` otherwise
     * @since 0.1.0
     */
    val isRegistered: Boolean

    /**
     * Unregisters the listener associated with this registration.
     *
     * This operation has no effect if the registration is already closed.
     * @since 0.1.0
     */
    fun unregister()

    /**
     * Unregisters the listener associated with this registration.
     *
     * This method delegates to [unregister].
     * @since 0.1.0
     */
    override fun close() {
        unregister()
    }

}