package io.github.deopping.bamboo.plugin.api

/**
 * Represents a platform-independent Bamboo plugin.
 *
 * A Bamboo plugin defines application lifecycle callbacks without depending
 * directly on a Minecraft server platform.
 *
 * Platform integrations are responsible for creating the plugin and invoking
 * its lifecycle methods.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface BambooPlugin {

    /**
     * The context provided to this plugin.
     * @since 0.1.0
     */
    val context: PluginContext

    /**
     * The current lifecycle state of this plugin.
     * @since 0.1.0
     */
    val state: PluginState

    /**
     * Called when the plugin is loaded.
     *
     * This is intended for initialization that must occur
     * before the plugin is enabled.
     *
     * @since 0.1.0
     */
    fun onLoad() {}

    /**
     * Called when the plugin is enabled.
     * @since 0.1.0
     */
    fun onEnable() {}

    /**
     * Called when the plugin is reloading.
     *
     * @since 0.1.0
     */
    fun onReload() {}

    /**
     * Called when the plugin is disabled.
     *
     * @since 0.1.0
     */
    fun onDisable() {}

}