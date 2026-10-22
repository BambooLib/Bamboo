package io.github.deopping.bamboo.packets.api

/**
 * Represents platform-independent information about a network packet.
 *
 * Implementations expose only the information required by Bamboo, without
 * exposing platform-specific packet classes or implementation details.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketView {

    /**
     * Gets the direction in which this packet travels
     * @since 0.1.0
     */
    val direction: PacketDirection

}