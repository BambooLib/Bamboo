package io.github.deopping.bamboo.configuration.internal.backends.impl

import com.electronwill.nightconfig.core.UnmodifiableConfig
import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.hocon.HoconFormat
import com.electronwill.nightconfig.json.JsonFormat
import com.electronwill.nightconfig.toml.TomlFormat
import com.electronwill.nightconfig.yaml.YamlFormat
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackend
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import java.nio.file.Path
import java.util.Optional

internal class NightConfigBackend(
    private val path: Path,
    private val format: ConfigFormat,
) : ConfigBackend {
    private val config: FileConfig = createFormat()

    override fun load() {
        config.load()
    }

    override fun save() {
        config.save()
    }

    override fun contains(path: List<String>): Boolean = config.contains(path)

    override fun get(path: List<String>): Optional<Any> = Optional.ofNullable(config.get<Any?>(path))

    override fun getSection(path: List<String>): Optional<Map<String, Any?>> {
        val value =
            config.get<Any?>(path)
                ?: return Optional.empty()

        return Optional.ofNullable(toSectionMap(value))
    }

    override fun set(
        path: List<String>,
        value: Any,
    ) {
        config.set<Any>(path, value)
    }

    override fun remove(path: List<String>): Boolean {
        if (!config.contains(path)) {
            return false
        }

        config.remove<Any?>(path)
        return true
    }

    override fun getComment(path: List<String>): Optional<String> =
        when (val current = config) {
            is CommentedFileConfig ->
                Optional.ofNullable(current.getComment(path))

            else -> Optional.empty()
        }

    override fun setComment(
        path: List<String>,
        comment: String,
    ) {
        val commentedConfig =
            config as? CommentedFileConfig
                ?: throw UnsupportedOperationException(
                    "Comments are not supported by $format.",
                )

        commentedConfig.setComment(path, comment)
    }

    override fun removeComment(path: List<String>): Boolean {
        val commentedConfig =
            config as? CommentedFileConfig
                ?: return false

        if (commentedConfig.getComment(path) == null) {
            return false
        }

        commentedConfig.setComment(path, null)
        return true
    }

    override fun close() {
        config.close()
    }

    private fun createFormat(): FileConfig =
        when (format) {
            ConfigFormat.YAML ->
                FileConfig.builder(path, YamlFormat.defaultInstance())
                    .preserveInsertionOrder()
                    .sync()
                    .build()

            ConfigFormat.JSON ->
                FileConfig.builder(path, JsonFormat.fancyInstance())
                    .preserveInsertionOrder()
                    .sync()
                    .build()

            ConfigFormat.HOCON ->
                CommentedFileConfig.builder(path, HoconFormat.instance())
                    .preserveInsertionOrder()
                    .sync()
                    .build()

            ConfigFormat.TOML ->
                CommentedFileConfig.builder(path, TomlFormat.instance())
                    .preserveInsertionOrder()
                    .sync()
                    .build()
        }

    private fun toSectionMap(value: Any): Map<String, Any?>? =
        when (value) {
            is UnmodifiableConfig -> {
                val entries = linkedMapOf<String, Any?>()

                for (entry in value.entrySet()) {
                    entries[entry.key] = normalizeValue(entry.getValue())
                }

                entries
            }

            is Map<*, *> -> normalizeMap(value)
            else -> null
        }

    private fun normalizeMap(value: Map<*, *>): Map<String, Any?> {
        val entries = linkedMapOf<String, Any?>()

        for ((key, child) in value) {
            val stringKey =
                key as? String
                    ?: throw ConfigException(
                        "Configuration section contains a non-string key.",
                    )

            entries[stringKey] = normalizeValue(child)
        }

        return entries
    }

    private fun normalizeValue(value: Any?): Any? =
        when (value) {
            is UnmodifiableConfig ->
                toSectionMap(value)
                    ?: throw ConfigException(
                        "Unable to convert a nested configuration section.",
                    )

            is Map<*, *> -> normalizeMap(value)
            is List<*> -> value.map(::normalizeValue)
            else -> value
        }
}
