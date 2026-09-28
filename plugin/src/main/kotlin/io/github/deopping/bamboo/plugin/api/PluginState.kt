package io.github.deopping.bamboo.plugin.api

/**
 * Represents the lifecycle state of a Bamboo plugin.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class PluginState {

    /**
     * The plugin has not yet been loaded.
     * @since 0.1.0
     */
    UNLOADED,

    /**
     * The plugin is currently loading.
     * @since 0.1.0
     */
    LOADING,

    /**
     * The plugin has loaded but has not yet been enabled.
     * @since 0.1.0
     */
    LOADED,

    /**
     * The plugin is currently being enabled.
     * @since 0.1.0
     */
    ENABLING,

    /**
     * The plugin is enabled and operational.
     * @since 0.1.0
     */
    ENABLED,

    /**
     * The plugin is currently being disabled.
     * @since 0.1.0
     */
    DISABLING,

    /**
     * The plugin has been disabled.
     * @since 0.1.0
     */
    DISABLED,

    /**
     * The plugin failed to load or enable.
     * @since 0.1.0
     */
    FAILED

}