package io.github.deopping.bamboo.packets.api

/**
 * Defines the order in which packet listeners are invoked.
 *
 * Listeners are invoked in ascending priority order, beginning with
 * [LOWEST] and ending with [MONITOR]. Listeners with the same priority
 * should be invoked in registration order.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class PacketPriority {

    /**
     * The earliest listener priority.
     * @since 0.1.0
     */
    LOWEST,

    /**
     * A low listener priority.
     * @since 0.1.0
     */
    LOW,

    /**
     * The default listener priority.
     * @since 0.1.0
     */
    NORMAL,

    /**
     * A high listener priority.
     * @since 0.1.0
     */
    HIGH,

    /**
     * The latest priority intended for listeners that modify packet events.
     * @since 0.1.0
     */
    HIGHEST,

    /**
     * The final listener priority, intended for observing the event.
     *
     * Listeners at this priority should not modify the event or its
     * cancellation state.
     * @since 0.1.0
     */
    MONITOR

}