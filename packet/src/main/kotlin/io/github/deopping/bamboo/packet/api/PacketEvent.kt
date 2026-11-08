package io.github.deopping.bamboo.packet.api

import io.github.deopping.bamboo.event.api.Cancellable
import io.github.deopping.bamboo.event.api.Event
import io.github.deopping.bamboo.player.api.Player

/**
 * Base class for packet-related events.
 *
 * @property player the player associated with the packet
 * @property packet the platform-independent packet view
 * @author DeOpping
 * @since 1.0.0
 */
public abstract class PacketEvent(
    public val player: Player,
    public open val packet: PacketView,
) : Event, Cancellable {
    public final override var isCanceled: Boolean = false
}
