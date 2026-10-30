package io.github.deopping.bamboo.plugin.api

import java.util.Optional

/**
 * Manages the lifecycle of a Bamboo plugin.
 *
 * Lifecycle operations must be invoked sequentially by the host platform.
 * The host platform is responsible for invoking lifecycle operations on
 * the appropriate thread.
 *
 * @property plugin the plugin managed by this runtime
 * @property context the context provided to the plugin
 * @property state the current lifecycle state
 * @property failure the failure encountered during lifecycle execution, if any
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface PluginRuntime {
    public val plugin: BambooPlugin

    public val context: PluginContext

    public val state: PluginState

    public val failure: Optional<Throwable>

    /**
     * Loads the plugin.
     *
     * @throws IllegalStateException if the plugin cannot be loaded from its current state
     * @throws Throwable if the plugin's loading callback fails
     * @since 1.0.0
     */
    public fun load(): Unit

    /**
     * Enables the plugin.
     *
     * @throws IllegalStateException if the plugin cannot be enabled from its current state
     * @throws Throwable if the plugin's enabling callback fails
     * @since 1.0.0
     */
    public fun enable(): Unit

    /**
     * Disables the plugin.
     *
     * @throws IllegalStateException if the plugin cannot be disabled from its current state
     * @throws Throwable if the plugin's disabling callback fails
     * @since 1.0.0
     */
    public fun disable(): Unit
}
