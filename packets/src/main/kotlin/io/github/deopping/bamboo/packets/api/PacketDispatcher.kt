package io.github.deopping.bamboo.packets.api

/**
 * Dispatches packet events to registered packet event handlers.
 *
 * Events are delivered according to handler priority. Handlers configured
 * to ignore canceled events are skipped when the event is canceled.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PacketDispatcher {

    /**
     * Dispatches an event to its applicable registered handlers.
     *
     * @param event the event to dispatch
     */
    fun dispatch(event: PacketEvent<out PacketView>)

}