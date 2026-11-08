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
    /**
     * Executes before all other priorities.
     * @since 1.0.0
     */
    LOWEST,

    /**
     * Executes at a low priority.
     * @since 1.0.0
     */
    LOW,

    /**
     * Executes at the default priority.
     * @since 1.0.0
     */
    NORMAL,

    /**
     * Executes at a high priority.
     * @since 1.0.0
     */
    HIGH,

    /**
     * Executes after high-priority handlers.
     * @since 1.0.0
     */
    HIGHEST,

    /**
     * Executes last and should be used for observing event results.
     *
     * Handlers at this priority should not modify event state.
     * @since 1.0.0
     */
    MONITOR,
}
