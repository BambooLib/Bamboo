package io.github.deopping.bamboo.packets.api

/**
 * Identifies an object that contains packet event handlers.
 *
 * Implementations may register a listener through [PacketRegistry].
 * Handler methods must be annotated with [PacketEventHandler].
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketListener