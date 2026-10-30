package io.github.deopping.bamboo.plugin.api

/**
 * Represents a plugin managed by Bamboo.
 *
 * Implementations provide lifecycle callbacks that are invoked by the
 * platform-independent plugin runtime.
 *
 * @property meta the meta of this plugin
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface BambooPlugin {
    public val meta: PluginMeta

    /**
     * Called when the plugin begins loading.
     *
     * This is intended for loading configuration, preparing resources,
     * and initializing platform-independent components.
     *
     * @param context the context provided to this plugin
     * @since 1.0.0
     */
    public fun onLoad(context: PluginContext): Unit = Unit

    /**
     * Called when the plugin is being enabled.
     *
     * @param context the context provided to this plugin
     * @since 1.0.0
     */
    public fun onEnable(context: PluginContext): Unit = Unit

    /**
     * Called when the plugin is being disabled.
     *
     * @param context the context provided to this plugin
     * @since 1.0.0
     */
    public fun onDisable(context: PluginContext): Unit = Unit
}
