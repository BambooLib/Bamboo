package io.github.deopping.bamboo.plugin.api

import java.nio.file.Path
import java.util.logging.Logger

/**
 * Provides platform-independent information and resources to a Bamboo plugin.
 *
 * @property meta the meta of the plugin
 * @property dataDirectory the directory assigned to the plugin for persistent data
 * @property logger the logger assigned to the plugin
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface PluginContext {
    public val meta: PluginMeta

    public val dataDirectory: Path

    public val logger: Logger
}
