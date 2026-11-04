package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigFormat
import io.github.deopping.bamboo.configuration.api.backends.ConfigBackend
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigValueException
import io.github.deopping.bamboo.configuration.internal.updater.FileFingerprint
import java.nio.file.Path
import java.util.Collections
import java.util.Optional
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class DefaultConfig(
    override val path: Path,
    override val format: ConfigFormat,
    private val backend: ConfigBackend,
) : Config {
    private val lock = ReentrantReadWriteLock()

    private var dirty: Boolean = false
    private var closed: Boolean = false
    private var fingerprint: FileFingerprint = FileFingerprint.NON_EXISTING

    override val isDirty: Boolean
        get() = lock.read { dirty }

    init {
        try {
            backend.load()
            fingerprint =
                FileFingerprint.capture(
                    path = path,
                    includeContentHash = true,
                )
        } catch (exception: Exception) {
            val failure = failure("load", exception)

            try {
                backend.close()
            } catch (closeException: Exception) {
                failure.addSuppressed(closeException)
            }

            closed = true
            throw failure
        }
    }

    override fun contains(path: String): Boolean =
        readValue(
            path = path,
            operation = "check",
            action = backend::contains,
        )

    override fun get(path: String): Optional<Any> =
        readValue(
            path = path,
            operation = "read",
            action = backend::get,
        )

    override fun getString(path: String): Optional<String> =
        get(path).map { value ->
            value as? String
                ?: throw ConfigValueException(path, "String", value)
        }

    override fun getString(
        path: String,
        defaultValue: String,
    ): String = getString(path).orElseGet { defaultValue }

    override fun getBoolean(path: String): Optional<Boolean> =
        get(path).map { value ->
            when (value) {
                is Boolean -> value
                is String ->
                    value.toBooleanStrictOrNull()
                        ?: throw ConfigValueException(path, "Boolean", value)
                else -> throw ConfigValueException(path, "Boolean", value)
            }
        }

    override fun getBoolean(
        path: String,
        defaultValue: Boolean,
    ): Boolean = getBoolean(path).orElseGet { defaultValue }

    override fun getInt(path: String): Optional<Int> =
        get(path).map { value ->
            val parsed =
                when (value) {
                    is Int -> value

                    is Byte, is Short, is Long ->
                        value.toString().toIntOrNull()

                    is String -> value.toIntOrNull()

                    else -> null
                }

            parsed ?: throw ConfigValueException(path, "Int", value)
        }

    override fun getInt(
        path: String,
        defaultValue: Int,
    ): Int = getInt(path).orElseGet { defaultValue }

    override fun getLong(path: String): Optional<Long> =
        get(path).map { value ->
            val parsed =
                when (value) {
                    is Long -> value

                    is Byte, is Short, is Int ->
                        value.toString().toLongOrNull()

                    is String -> value.toLongOrNull()

                    else -> null
                }

            parsed ?: throw ConfigValueException(path, "Long", value)
        }

    override fun getLong(
        path: String,
        defaultValue: Long,
    ): Long = getLong(path).orElseGet { defaultValue }

    override fun getDouble(path: String): Optional<Double> =
        get(path).map { value ->
            val parsed =
                when (value) {
                    is Double -> value
                    is Number -> value.toDouble()
                    is String -> value.toDoubleOrNull()
                    else -> null
                }

            if (parsed == null || !parsed.isFinite()) {
                throw ConfigValueException(path, "finite Double", value)
            }

            parsed
        }

    override fun getDouble(
        path: String,
        defaultValue: Double,
    ): Double = getDouble(path).orElseGet { defaultValue }

    override fun getList(path: String): Optional<List<Any?>> =
        get(path).map { value ->
            val list =
                value as? List<*>
                    ?: throw ConfigValueException(path, "List", value)

            list.toList()
        }

    override fun getAsStringList(path: String): Optional<List<String>> =
        getList(path).map { values ->
            values.mapIndexed { index, value ->
                value as? String
                    ?: throw ConfigValueException(
                        "$path[$index]",
                        "String",
                        value ?: NullConfigurationValue,
                    )
            }
        }

    override fun getSection(path: String): Optional<Map<String, Any?>> {
        val parsedPath = ConfigPath.parse(path)

        return lock.read {
            ensureOpen()

            try {
                val value =
                    backend.get(parsedPath)
                        ?: return@read Optional.empty()

                val section =
                    backend.getSection(parsedPath)
                        .orElseThrow {
                            ConfigValueException(
                                path,
                                "configuration section",
                                value,
                            )
                        }

                val snapshot =
                    Collections.unmodifiableMap(
                        LinkedHashMap(section),
                    )

                Optional.of(snapshot)
            } catch (exception: ConfigException) {
                throw exception
            } catch (exception: Exception) {
                throw failure("read section '$path'", exception)
            }
        }
    }

    override fun set(
        path: String,
        value: Any,
    ) {
        writeValue(path, "write") { parsedPath ->
            val current = backend.get(parsedPath)

            if (current.isEmpty || current.get() != value) {
                backend.set(parsedPath, value)
                dirty = true
            }
        }
    }

    override fun remove(path: String): Boolean {
        return writeValue(path, "remove") { parsedPath ->
            val removed = backend.remove(parsedPath)

            if (removed) {
                dirty = true
            }

            removed
        }
    }

    override fun getComment(path: String): Optional<String> =
        readValue(
            path = path,
            operation = "read comment",
            action = backend::getComment,
        )

    override fun setComment(
        path: String,
        comment: String,
    ) {
        writeValue(path, "write comment") { parsedPath ->
            val existing = backend.getComment(parsedPath)

            if (existing.isPresent && existing.get() == comment) {
                return@writeValue
            }

            backend.setComment(parsedPath, comment)
            dirty = true
        }
    }

    override fun removeComment(path: String) {
        writeValue(path, "remove comment") { parsedPath ->
            if (backend.removeComment(parsedPath)) {
                dirty = true
            }
        }
    }

    override fun save() {
        lock.write {
            ensureOpen()

            if (!dirty) {
                return@write
            }

            try {
                backend.save()

                val newFingerprint =
                    FileFingerprint.capture(
                        path = path,
                        includeContentHash = true,
                    )

                fingerprint = newFingerprint
                dirty = false
            } catch (exception: Exception) {
                throw failure("save", exception)
            }
        }
    }

    override fun reload(discardUnsavedChanges: Boolean) {
        lock.write {
            ensureOpen()

            if (dirty && !discardUnsavedChanges) {
                throw ConfigException(
                    "Cannot reload '$path': unsaved changes exist. " +
                        "Save them or explicitly discard them.",
                )
            }

            try {
                backend.load()

                fingerprint =
                    FileFingerprint.capture(
                        path = path,
                        includeContentHash = true,
                    )

                dirty = false
            } catch (exception: Exception) {
                throw failure("reload", exception)
            }
        }
    }

    override fun reloadIfChanged(): Boolean {
        lock.write {
            ensureOpen()

            try {
                val observedFingerprint =
                    FileFingerprint.capture(
                        path = path,
                        includeContentHash = true,
                    )

                val contentUnchanged =
                    observedFingerprint.exists == fingerprint.exists &&
                        observedFingerprint.contentHash == fingerprint.contentHash

                if (contentUnchanged || dirty) {
                    return false
                }

                backend.load()

                dirty = false
                return true
            } catch (exception: Exception) {
                throw failure("check for external changes", exception)
            }
        }
    }

    override fun close() {
        lock.write {
            if (closed) {
                return@write
            }

            var failure: ConfigException? = null

            if (dirty) {
                try {
                    backend.save()

                    fingerprint =
                        FileFingerprint.capture(
                            path = path,
                            includeContentHash = true,
                        )

                    dirty = false
                } catch (exception: Exception) {
                    failure = failure("save before closing", exception)
                }
            }

            try {
                backend.close()
            } catch (exception: Exception) {
                val closeFailure = failure("close", exception)
                val primaryFailure = failure

                if (primaryFailure == null) {
                    failure = closeFailure
                } else {
                    primaryFailure.addSuppressed(closeFailure)
                }
            } finally {
                closed = true
            }

            failure?.let { throw it }
        }
    }

    private fun <T> readValue(
        path: String,
        operation: String,
        action: (List<String>) -> T,
    ): T {
        val parsedPath = ConfigPath.parse(path)

        return lock.read {
            ensureOpen()

            try {
                action(parsedPath)
            } catch (exception: Exception) {
                throw failure("$operation '$path'", exception)
            }
        }
    }

    private fun <T> writeValue(
        path: String,
        operation: String,
        action: (List<String>) -> T,
    ): T {
        val parsedPath = ConfigPath.parse(path)

        return lock.write {
            ensureOpen()

            try {
                action(parsedPath)
            } catch (exception: Exception) {
                throw failure("$operation '$path'", exception)
            }
        }
    }

    private fun ensureOpen() {
        if (closed) {
            throw ConfigException("Configuration is closed: $path")
        }
    }

    private fun failure(
        operation: String,
        cause: Exception,
    ): ConfigException {
        return ConfigException(
            "Failed to $operation configuration '$path'.",
            cause,
        )
    }

    private companion object {
        /**
         * Allows a null element in a list to be represented in the
         * exception without exposing a nullable exception parameter.
         */
        private val NullConfigurationValue: Any = "<null>"
    }
}
