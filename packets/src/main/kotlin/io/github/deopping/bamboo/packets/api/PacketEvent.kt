package io.github.deopping.bamboo.packets.api

import java.util.UUID

/**
 * Represents a packet being processed by the packet system.
 *
 * A packet event provides the identity of the player associated with the
 * packet, the platform-independent packet view, and its cancellation state
 * when cancellation is supported.
 *
 * Whether an event supports cancellation depends on the packet and the
 * underlying platform implementation. Attempting to cancel an event that
 * does not support cancellation throws an [UnsupportedOperationException].
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketEvent<P : PacketView> {

    /**
     * Gets the unique identifier of the player associated with this event.
     * @since 0.1.0
     */
    val playerId: UUID

    /**
     * Gets the platform-independent view of the packet being processed.
     * @since 0.1.0
     */
    val packet: P

    /**
     * Gets whether this event supports cancellation.
     * @return `true` if the event can be canceled, `false` otherwise
     * @since 0.1.0
     */
    val cancelable: Boolean
        get() = false

    /**
     * Gets whether this event has been canceled.
     *
     * @return `true` if the event has been canceled, `false` otherwise
     * @since 0.1.0
     */
    val isCanceled: Boolean
        get() = false

    /**
     * Changes the cancellation state of this event.
     *
     * Implementations must throw [UnsupportedOperationException] when
     * cancellation is not supported and the requested state is `true`.
     *
     * @param canceled `true` to cancel the event; `false` to uncancel it
     * @throws UnsupportedOperationException if cancellation is unsupported
     */
    fun setCanceled(canceled: Boolean) {
        if (isCanceled) {
            throw UnsupportedOperationException(
                "This packet event cannot be canceled."
            )
        }
    }

}