package io.github.deopping.bamboo.packet.api

/**
 * Describes the result of sending a packet.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public enum class PacketSendResult {
    /** The packet was transmitted through the platform's packet transport. */
    SENT,

    /** The packet was canceled by a packet send event. */
    CANCELED,
}
