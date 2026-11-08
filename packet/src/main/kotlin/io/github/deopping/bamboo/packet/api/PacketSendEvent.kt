package io.github.deopping.bamboo.packet.api

import io.github.deopping.bamboo.player.api.Player

/**
 * Called when a packet is about to be sent to a client.
 *
 * Cancelling this event prevents the packet from being transmitted.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class PacketSendEvent(
    player: Player,
    override val packet: OutboundPacketView,
) : PacketEvent(player, packet)
