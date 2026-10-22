package io.github.deopping.bamboo.packets.api

/**
 * Represents the direction in which a network packet travels related to
 * the server.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class PacketDirection {

    /**
     * A packet sent from a `client` to the `server`
     * @since 0.1.0
     */
    INCOMING,

    /**
     * A packet sent from the `server` to a `client`.
     * @since 0.1.0
     */
    OUTGOING

}