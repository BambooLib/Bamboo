package io.github.deopping.bamboo.packet.api

import io.github.deopping.bamboo.player.api.Player
import java.util.concurrent.CompletableFuture

/**
 * Provides access to packet sending functionality.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface PacketService {
    /**
     * Checks whether an outgoing packet type is supported by the current platform.
     *
     * @param packetType the outgoing packet view type
     * @return `true` if the platform supports sending this packet type
     * @since 1.0.0
     */
    public fun supports(packetType: Class<out OutboundPacketView>): Boolean

    /**
     * Sends a packet to a player.
     *
     * The packet is processed through the platform's outgoing packet interception
     * pipeline, including [PacketSendEvent].
     *
     * @param player the receiving player
     * @param packet the packet to send
     * @return a future completed with the send result
     * @since 1.0.0
     */
    public fun send(
        player: Player,
        packet: OutboundPacketView,
    ): CompletableFuture<PacketSendResult>
}
