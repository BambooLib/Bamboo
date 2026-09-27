package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.ConfigFormat
import java.nio.file.Path

internal interface ConfigBackend {

    val path: Path
    val format: ConfigFormat

    fun contains(path: List<String>): Boolean

    fun get(path: List<String>): Any?

    fun set(path: List<String>, value: Any?)

    fun remove(path: List<String>): Boolean

    fun getComment(path: List<String>): String?

    fun setComment(path: List<String>, comment: String?)

    fun load()

    fun save()

    fun close()

}