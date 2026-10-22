package io.github.deopping.bamboo.packets.api

/**
 * Manages registration of packet listeners.
 *
 * Registering the same listener instance more than once should not create
 * duplicate handlers.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketRegistry {

    /**
     * Registers the annotated handlers declared by a listener.
     *
     * @param listener the listener to register
     * @return a handle for unregistering the listener
     * @throws IllegalArgumentException if a handler declaration is valid
     */
    fun register(listener: PacketListener): PacketRegistration

}