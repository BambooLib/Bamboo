package io.github.deopping.bamboo.packet.api

/**
 * Represents a packet sent from the server to a client.
 *
 * Implementations can also be constructed by plugins and passed to
 * [PacketService.send].
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface OutboundPacketView : PacketView
