package io.github.deopping.bamboo.packets.api

/**
 * Marks a method as a handler for packet events.
 *
 * Annotated methods must accept exactly one parameter whose type implements
 * [PacketEvent].
 *
 * @property priority the priority at which the handler is invoked
 * @property ignoreCancelled whether the handler should be skipped when the event is canceled
 * @author DeOpping
 * @since 0.1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PacketEventHandler(

    /**
     * The priority at which the handler is invoked.
     * @since 0.1.0
     */
    val priority: PacketPriority = PacketPriority.NORMAL,

    /**
     * Whether the handler should be skipped when the event is canceled.
     * @since 0.1.0
     */
    val ignoreCancelled: Boolean = false

)
