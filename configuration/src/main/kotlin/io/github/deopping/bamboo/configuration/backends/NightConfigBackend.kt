package io.github.deopping.bamboo.configuration.backends

import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.hocon.HoconFormat
import com.electronwill.nightconfig.json.JsonFormat
import com.electronwill.nightconfig.toml.TomlFormat
import com.electronwill.nightconfig.yaml.YamlFormat
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.internal.ConfigBackend
import java.nio.file.Path

internal class NightConfigBackend(
    override val path: Path,
    override val format: ConfigFormat
) : ConfigBackend {

    private val config: FileConfig = createFormat()

    override fun load() {
        config.load()
    }

    override fun save() {
        config.save()
    }

    override fun contains(path: List<String>): Boolean {
        return config.contains(path)
    }

    override fun get(path: List<String>): Any? {
        return config.get(path)
    }

    override fun set(path: List<String>, value: Any?) {
        config.set<Any?>(path, value)
    }

    override fun remove(path: List<String>): Boolean {
        if (!config.contains(path)) {
            return false
        }

        config.remove<Any?>(path)
        return true
    }

    override fun getComment(path: List<String>): String? {
        return when (val config = config) {
            is CommentedFileConfig -> config.getComment(path)
            else -> null
        }
    }

    override fun setComment(path: List<String>, comment: String?) {
        when (val config = config) {
            is CommentedFileConfig -> config.setComment(path, comment)
            else -> throw UnsupportedOperationException("Comments are not supported by $format.")
        }
    }

    override fun close() {
        config.close()
    }

    private fun createFormat(): FileConfig {
        return when (format) {
            ConfigFormat.YAML -> FileConfig
                .builder(path, YamlFormat.defaultInstance())
                .preserveInsertionOrder()
                .sync()
                .build()

            ConfigFormat.JSON -> FileConfig
                .builder(path, JsonFormat.fancyInstance())
                .preserveInsertionOrder()
                .sync()
                .build()

            ConfigFormat.HOCON -> CommentedFileConfig
                .builder(path, HoconFormat.instance())
                .preserveInsertionOrder()
                .sync()
                .build()

            ConfigFormat.TOML -> CommentedFileConfig
                .builder(path, TomlFormat.instance())
                .preserveInsertionOrder()
                .sync()
                .build()
        }
    }

}