package io.github.deopping.bamboo.configuration.backends

import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.nightconfig.toml.TomlFormat
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.internal.ConfigBackend
import java.nio.file.Path

internal class NightConfigTomlBackend(
    override val path: Path
) : ConfigBackend {

    override val format: ConfigFormat = ConfigFormat.TOML

    private val config = CommentedFileConfig
        .builder(path, TomlFormat.instance())
        .preserveInsertionOrder()
        .sync()
        .build()

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
        return config.getComment(path)
    }

    override fun setComment(path: List<String>, comment: String?) {
        config.setComment(path, comment)
    }

    override fun close() {
        config.close()
    }

}