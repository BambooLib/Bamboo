package io.github.deopping.bamboo.configuration.backends

import io.github.deopping.bamboo.configuration.api.ConfigException
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.internal.ConfigBackend
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.gson.GsonConfigurationLoader
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

internal class ConfigurateBackend(
    override val path: Path,
    override val format: ConfigFormat
) : ConfigBackend {

    private val loader: ConfigurationLoader<out ConfigurationNode> = createLoader()

    private lateinit var root: ConfigurationNode

    override fun load() {
        try {
            root = loader.load()
        }
        catch (exception: Exception) {
            throw ConfigException(
                "Failed to load configuration: $path",
                exception
            )
        }
    }

    override fun save() {
        try {
            loader.save(root)
        }
        catch (exception: Exception) {
            throw ConfigException(
                "Failed to save configuration: $path",
                exception
            )
        }
    }

    override fun contains(path: List<String>): Boolean {
        return root.hasChild(path)
    }

    override fun get(path: List<String>): Any? {
        return root.node(path).raw()
    }

    override fun set(path: List<String>, value: Any?) {
        root.node(path).raw(value)
    }

    override fun remove(path: List<String>): Boolean {
        if (path.isEmpty()) {
            return false
        }

        val node = root.node(path)

        if (node.virtual()) {
            return false
        }

        val parent = node.parent()
            ?: return false

        return parent.removeChild(node.key())
            .let { true }
    }

    override fun getComment(path: List<String>): String? {
        val node = root.node(path)
        return (node as? CommentedConfigurationNode)?.comment()
    }

    override fun setComment(path: List<String>, comment: String?) {
        val node = root.node(path)
        val commentedNode = node as? CommentedConfigurationNode
            ?: throw UnsupportedOperationException(
                "Comments are not supported by $format"
            )

        commentedNode.comment(comment)
    }

    override fun close() {
        // Configurate loaders do not own a persistent resource here.
    }

    private fun createLoader(): ConfigurationLoader<out ConfigurationNode> {
        return when (format) {
            ConfigFormat.YAML -> YamlConfigurationLoader.builder()
                .path(path)
                .build()

            ConfigFormat.JSON -> GsonConfigurationLoader.builder()
                .path(path)
                .build()

            ConfigFormat.HOCON -> HoconConfigurationLoader.builder()
                .path(path)
                .build()

            ConfigFormat.TOML -> throw IllegalArgumentException(
                "TOML must use NightConfigBackend"
            )
        }
    }

}