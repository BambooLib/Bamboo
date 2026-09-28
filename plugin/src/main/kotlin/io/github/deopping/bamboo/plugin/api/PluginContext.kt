package io.github.deopping.bamboo.plugin.api

import java.nio.file.Path

/**
 * Provides platform-independent services and information to a Bamboo plugin.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface PluginContext {

    /**
     * The plugin's data directory.
     * @since 0.1.0
     */
    val dataDirectory: Path

    /**
     * The plugin's class loader.
     * @since 0.1.0
     */
    val classLoader: ClassLoader

    /**
     * The plugin's logger.
     * @since 0.1.0
     */
    val logger: PluginLogger

}