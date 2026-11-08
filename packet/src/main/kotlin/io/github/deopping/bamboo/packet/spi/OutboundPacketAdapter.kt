package io.github.deopping.bamboo.packet.spi

import io.github.deopping.bamboo.packet.api.OutboundPacketView
import io.github.deopping.bamboo.player.api.Player

/**
 * Converts between a platform packet and an outgoing Bamboo packet view.
 *
 * @param N the platform's native packet type
 * @param V the Bamboo outgoing packet view type
 * @author DeOpping
 * @since 1.0.0
 */
public interface OutboundPacketAdapter<N : Any, V : OutboundPacketView> : PacketAdapter<N, V> {
    /**
     * Converts an outgoing Bamboo packet view into a native packet.
     *
     * @param player the player who will receive the packet
     * @param packet the outgoing packet view
     * @return the native packet
     * @since 1.0.0
     */
    public fun toNativePacket(
        player: Player,
        packet: V,
    ): N
}
