package io.github.deopping.bamboo.plugin.api

/**
 * Represents the lifecycle state of a Bamboo plugin.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public enum class PluginState {
    /**
     * The plugin has not started loading.
     * @since 1.0.0
     */
    UNLOADED,

    /**
     * The plugin is currently loading.
     * @since 1.0.0
     */
    LOADING,

    /**
     * The plugin has successfully loaded.
     * @since 1.0.0
     */
    LOADED,

    /**
     * The plugin is currently enabling.
     * @since 1.0.0
     */
    ENABLING,

    /**
     * The plugin has successfully enabled.
     * @since 1.0.0
     */
    ENABLED,

    /**
     * The plugin is currently disabling.
     * @since 1.0.0
     */
    DISABLING,

    /**
     * The plugin has successfully disabled.
     * @since 1.0.0
     */
    DISABLED,

    /**
     * The plugin encountered an unrecoverable lifecycle failure.
     * @since 1.0.0
     */
    FAILED,
}
