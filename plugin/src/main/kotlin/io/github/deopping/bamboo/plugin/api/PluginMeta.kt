package io.github.deopping.bamboo.plugin.api

/**
 * Contains the descriptive information associated with a Bamboo plugin.
 *
 * @property id the unique identifier of the plugin
 * @property name the display name of the plugin
 * @property version the plugin version
 * @property apiVersion the api version of Minecraft the plugin uses (i.e. "26.3")
 * @property authors the authors of the plugin
 * @property description the description of the plugin
 *
 * @author DeOpping
 * @since 1.0.0
 */
public data class PluginMeta(
    public val id: String,
    public val name: String,
    public val version: String,
    public val apiVersion: String,
    public val authors: List<String> = emptyList(),
    public val description: String = "",
) {
    init {
        require(id.isNotBlank()) {
            "Plugin ID cannot be blank."
        }

        require(name.isNotBlank()) {
            "Plugin name cannot be blank."
        }

        require(version.isNotBlank()) {
            "Plugin version cannot be blank."
        }

        require(apiVersion.isNotBlank()) {
            "Plugin api version cannot be blank."
        }
    }
}
