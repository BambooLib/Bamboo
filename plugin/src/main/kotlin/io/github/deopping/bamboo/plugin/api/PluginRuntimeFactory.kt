package io.github.deopping.bamboo.plugin.api

import io.github.deopping.bamboo.plugin.internal.DefaultPluginRuntime

/**
 * Creates a plugin runtime instance.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public object PluginRuntimeFactory {
    /**
     * Creates a runtime for the specified plugin.
     *
     * @param plugin the plugin to manage
     * @param context the context provided to the plugin
     * @return a new plugin runtime
     * @since 1.0.0
     */
    public fun create(
        plugin: BambooPlugin,
        context: PluginContext,
    ): PluginRuntime {
        require(plugin.meta == context.meta) {
            "Plugin meta does not match the provided context."
        }

        return DefaultPluginRuntime(plugin, context)
    }
}
