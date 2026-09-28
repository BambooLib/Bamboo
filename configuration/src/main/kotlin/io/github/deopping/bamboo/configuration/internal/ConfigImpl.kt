package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigException
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class ConfigImpl(
    override val path: Path,
    override val format: ConfigFormat,
    private val backend: ConfigBackend
) : Config, ManagedConfig {

    private val lock = ReentrantReadWriteLock()

    @Volatile
    private var dirty: Boolean = false

    @Volatile
    private var closed: Boolean = false

    @Volatile
    private var fingerprint: FileFingerprint =
        FileFingerprint.NON_EXISTING

    override val isDirty: Boolean
        get() = dirty

    init {
        loadInitial()
    }

    override fun contains(path: String): Boolean {
        lock.read {
            ensureOpen()
            return backend.contains(ConfigPath.parse(path))
        }
    }

    override fun get(path: String): Any? {
        lock.read {
            ensureOpen()
            return backend.get(ConfigPath.parse(path))
        }
    }

    override fun getOptional(path: String): Optional<Any> {
        return Optional.ofNullable(get(path))
    }

    override fun getOr(path: String, def: Any?): Any? {
        return get(path) ?: def
    }

    override fun getString(path: String, def: String?): String? {
        return when (val value = get(path)) {
            null -> def
            is String -> value
            else -> value.toString()
        }
    }

    override fun getBoolean(path: String, def: Boolean?): Boolean? {
        return when (val value = get(path)) {
            null -> def
            is Boolean -> value
            is String -> value.toBooleanStrictOrNull() ?: def
            else -> def
        }
    }

    override fun getInt(path: String, def: Int?): Int? {
        return when (val value = get(path)) {
            null -> def
            is Int -> value
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: def
            else -> def
        }
    }

    override fun getLong(path: String, def: Long?): Long? {
        return when (val value = get(path)) {
            null -> def
            is Long -> value
            is Number -> value.toLong()
            is String -> value.toLongOrNull() ?: def
            else -> def
        }
    }

    override fun getDouble(path: String, def: Double?): Double? {
        return when (val value = get(path)) {
            null -> def
            is Double -> value
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: def
            else -> def
        }
    }

    override fun getList(path: String): List<Any?> {
        return get(path) as? List<Any?>
            ?: emptyList()
    }

    override fun getAsStringList(path: String): List<String> {
        return getList(path)
            .map { it?.toString() ?: "" }
    }

    override fun set(path: String, value: Any?) {
        lock.write {
            ensureOpen()

            val parsedPath = ConfigPath.parse(path)
            val current = backend.get(parsedPath)

            if (current == value) {
                return
            }

            backend.set(parsedPath, value)
            dirty = true
        }
    }

    override fun remove(path: String): Boolean {
        lock.write {
            ensureOpen()

            val removed = backend.remove(
                ConfigPath.parse(path)
            )

            if (removed) {
                dirty = true
            }

            return removed
        }
    }

    override fun getComment(path: String): String? {
        lock.read {
            ensureOpen()

            return backend.getComment(
                ConfigPath.parse(path)
            )
        }
    }

    override fun setComment(path: String, comment: String?) {
        lock.write {
            ensureOpen()

            val parsedPath = ConfigPath.parse(path)
            val current = backend.getComment(parsedPath)

            if (current == comment) {
                return
            }

            backend.setComment(parsedPath, comment)
            dirty = true
        }
    }

    override fun save() {
        lock.write {
            ensureOpen()

            if (!dirty) {
                return
            }

            backend.save()

            dirty = false

            fingerprint = FileFingerprint.capture(
                path,
                includeContentHash = true
            )
        }
    }

    override fun reload() {
        lock.write {
            ensureOpen()
            reloadInternal()
        }
    }

    override fun close() {
        lock.write {
            if (closed) {
                return
            }

            backend.close()
            closed = true
        }
    }

    override fun reloadIfChanged(): Boolean {
        lock.write {
            ensureOpen()

            val quickFingerprint = FileFingerprint.capture(path)
            val currentFingerprint = fingerprint

            if (quickFingerprint.exists == currentFingerprint.exists &&
                quickFingerprint.size == currentFingerprint.size &&
                quickFingerprint.lastModified == currentFingerprint.lastModified
            ) {
                return false
            }

            val fullFingerprint = FileFingerprint.capture(
                path,
                includeContentHash = true
            )

            if (fullFingerprint == fingerprint) {
                return false
            }

            // Never destroy unsaved local changes
            if (dirty) {
                return false
            }

            backend.load()
            fingerprint = fullFingerprint

            return true
        }
    }

    private fun loadInitial() {
        try {
            backend.load()

            dirty = false

            fingerprint = FileFingerprint.capture(
                path = path,
                includeContentHash = true
            )
        }
        catch (exception: ConfigException) {
            throw exception
        }
        catch (exception: Exception) {
            throw ConfigException(
                "Failed to load configuration: $path",
                exception
            )
        }
    }

    private fun reloadInternal() {
        try {
            backend.load()

            dirty = false

            fingerprint = FileFingerprint.capture(
                path,
                includeContentHash = true
            )
        }
        catch (exception: ConfigException) {
            throw exception
        }
        catch (exception: Exception) {
            throw ConfigException(
                "Failed to reload configuration: $path",
                exception
            )
        }
    }

    private fun ensureOpen() {
        check(!closed) {
            "Configuration is closed: $path"
        }
    }

}