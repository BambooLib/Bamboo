package io.github.deopping.bamboo.event.api

/**
 * Defines the execution priority of an event handler.
 *
 * Handlers are invoked in ascending priority order.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public enum class EventPriority {
    /** Executes before all other priorities. */
    LOWEST,

    /** Executes at a low priority. */
    LOW,

    /** Executes at the default priority. */
    NORMAL,

    /** Executes at a high priority. */
    HIGH,

    /** Executes after high-priority handlers. */
    HIGHEST,

    /**
     * Executes last and should be used for observing event results.
     *
     * Handlers at this priority should not modify event state.
     */
    MONITOR,
}
