package io.github.deopping.bamboo.packet.spi

import io.github.deopping.bamboo.packet.api.PacketView
import io.github.deopping.bamboo.player.api.Player

/**
 * Converts a platform's native packet into a Bamboo packet view.
 *
 * @param N the platform's native packet type
 * @param V the Bamboo packet view type
 * @author DeOpping
 * @since 1.0.0
 */
public interface PacketAdapter<N : Any, V : PacketView> {
    /**
     * Gets the native packet type handled by this adapter.
     *
     * @since 1.0.0
     */
    public val nativePacketType: Class<N>

    /**
     * Converts a native packet into a Bamboo packet view.
     *
     * @param player the player associated with the packet
     * @param packet the native packet
     * @return the platform-independent packet view
     * @since 1.0.0
     */
    public fun toView(
        player: Player,
        packet: N,
    ): V
}
