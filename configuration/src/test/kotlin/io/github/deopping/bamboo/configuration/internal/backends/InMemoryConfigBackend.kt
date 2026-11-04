package io.github.deopping.bamboo.configuration.internal.backends

import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackend
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger

internal class InMemoryConfigBackend(
    val path: Path,
    val format: ConfigFormat,
    initialValues: Map<List<String>, Any> = emptyMap(),
) : ConfigBackend {
    private val values = mutableMapOf<List<String>, Any?>()
    private val comments = mutableMapOf<List<String>, String?>()

    val loadCount = AtomicInteger()
    val saveCount = AtomicInteger()
    val closeCount = AtomicInteger()

    @Volatile
    var loadFailure: RuntimeException? = null

    @Volatile
    var saveFailure: RuntimeException? = null

    @Volatile
    var closeFailure: RuntimeException? = null

    @Volatile
    var closed: Boolean = false
        private set

    init {
        initialValues.forEach { (key, value) ->
            values[key.toList()] = value
        }
    }

    override fun load() {
        loadCount.incrementAndGet()
        loadFailure?.let { throw it }
    }

    override fun save() {
        saveCount.incrementAndGet()
        saveFailure?.let { throw it }
    }

    override fun contains(path: List<String>): Boolean {
        return values.containsKey(path)
    }

    override fun get(path: List<String>): Optional<Any> {
        return Optional.ofNullable(values[path])
    }

    override fun getSection(path: List<String>): Optional<Map<String, Any?>> {
        if (values.containsKey(path)) {
            val value =
                values[path] as? Map<*, *>
                    ?: return Optional.empty()

            return Optional.of(normalizeMap(value))
        }

        val section = linkedMapOf<String, Any?>()
        var found = false

        for ((storedPath, value) in values) {
            if (storedPath.size <= path.size || storedPath.take(path.size) != path) {
                continue
            }

            insertValue(
                target = section,
                path = storedPath.drop(path.size),
                value = value,
            )

            found = true
        }

        return if (found) Optional.of(section) else Optional.empty()
    }

    override fun set(
        path: List<String>,
        value: Any,
    ) {
        values[path] = value
    }

    override fun remove(path: List<String>): Boolean {
        return values.remove(path) != null
    }

    override fun getComment(path: List<String>): Optional<String> {
        return Optional.ofNullable(comments[path])
    }

    override fun setComment(
        path: List<String>,
        comment: String,
    ) {
        comments[path] = comment
    }

    override fun removeComment(path: List<String>): Boolean {
        return comments.remove(path) != null
    }

    override fun close() {
        closeCount.incrementAndGet()
        closed = true
        closeFailure?.let { throw it }
    }

    fun put(
        path: String,
        value: Any,
    ) {
        set(path.split('.'), value)
    }

    fun value(path: String): Optional<Any> {
        return get(path.split('.'))
    }

    private fun insertValue(
        target: MutableMap<String, Any?>,
        path: List<String>,
        value: Any?,
    ) {
        var current = target

        for (component in path.dropLast(1)) {
            val existing = current[component]

            current =
                when {
                    !current.containsKey(component) -> {
                        linkedMapOf<String, Any?>().also { current[component] = it }
                    }

                    existing is MutableMap<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        existing as MutableMap<String, Any?>
                    }

                    else -> throw IllegalStateException(
                        "Cannot construct configuration section: " +
                            "path component '$component' is already a value.",
                    )
                }
        }

        current[path.last()] = normalizeValue(value)
    }

    private fun normalizeMap(value: Map<*, *>): Map<String, Any?> {
        val result = linkedMapOf<String, Any?>()

        for ((key, child) in value) {
            val stringKey =
                key as? String
                    ?: throw IllegalStateException(
                        "Configuration section contains a non-string key.",
                    )

            result[stringKey] = normalizeValue(child)
        }

        return result
    }

    private fun normalizeValue(value: Any?): Any? =
        when (value) {
            is Map<*, *> -> normalizeMap(value)
            is List<*> -> value.map(::normalizeValue)
            else -> value
        }
}
