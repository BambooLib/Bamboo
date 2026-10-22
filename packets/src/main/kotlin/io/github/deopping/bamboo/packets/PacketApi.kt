package io.github.deopping.bamboo.packets

import io.github.deopping.bamboo.packets.api.PacketDispatcher
import io.github.deopping.bamboo.packets.api.PacketRegistry

/**
 * Entry point for Bamboo's packet system.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketApi {

    /**
     * The registry used to manage packet listeners.
     * @return the packet listener registry
     * @since 0.1.0
     */
    val registry: PacketRegistry

    /**
     * The dispatcher used to deliver packet events.
     * @return the packet event dispatcher
     * @since 0.1.0
     */
    val dispatcher: PacketDispatcher

}