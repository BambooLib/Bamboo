package io.github.deopping.bamboo.packet.api

import io.github.deopping.bamboo.player.api.Player

/**
 * Called when a packet is received from a client.
 *
 * Cancelling this event prevents the packet from being processed by the server.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class PacketReceiveEvent(
    player: Player,
    override val packet: InboundPacketView,
) : PacketEvent(player, packet)
