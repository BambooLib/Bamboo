package io.github.deopping.bamboo.configuration.api

import org.jetbrains.annotations.Contract
import java.lang.AutoCloseable
import java.nio.file.Path
import java.util.Optional

/**
 * Represents a loaded and managed configuration file.
 *
 * A configuration provides a format-independent API for reading, modifying,
 * commenting, saving, and reloading configuration data.
 *
 * Configuration values are addressed using dot-separated paths. For example,
 * `database.host` refers to the `host` value inside the `database` section.
 *
 * Changes made through this interface are kept in-memory until
 * [save] is called. The [isDirty] property indicates whether the
 * configuration contains changes that have not yet been persisted.
 *
 * Calling [reload] discards unsaved in-memory changes and reloads
 * the configuration from its backing file.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface Config : AutoCloseable {

    /**
     * The normalized path of the backing configuration file.
     * @since 0.1.0
     */
    val path: Path

    /**
     * The format used by this configuration.
     * @since 0.1.0
     */
    val format: ConfigFormat

    /**
     * Whether this configuration contains unsaved changes.
     * @return `true` when the in-memory configuration differs from the
     * persisted state
     * @since 0.1.0
     */
    val isDirty: Boolean

    /**
     * Determines whether a value exists at the specified path.
     * @param path dot-separated configuration path
     * @return `true` if the path exists, `false` otherwise
     * @since 0.1.0
     */
    fun contains(path: String): Boolean

    /**
     * Retrieves a raw value from the configuration.
     *
     * The returned type depends on the value stored at the persisted path.
     * Applications that require a specific type should use one of the typed
     * getter methods where appropriate.
     *
     * @param path dot-separated configuration path
     * @return the value at the specified path, or `null` when no value exists
     * @since 0.1.0
     */
    fun get(path: String): Any?

    /**
     * Retrieves a raw value wrapped in an [Optional].
     *
     * An empty optional is returned when no value exists at the specified path.
     *
     * @param path dot-separated configuration path
     * @return an optional containing the configured value, when present
     * @since 0.1.0
     */
    fun getOptional(path: String): Optional<Any>

    /**
     * Retrieves a raw value, returning a default when no value exists.
     *
     * @param path dot-separated configuration path
     * @param def value to return when the path has no configured value
     * @return the configured value, or [def] when absent
     * @since 0.1.0
     */
    @Contract("_, !null -> !null")
    fun getOr(path: String, def: Any?): Any?

    /**
     * Retrieves a string value.
     *
     * @param path dot-separated configuration path
     * @param def value to return when the path has no usable value
     * @return the configured string, or [def] when unavailable
     * @since 0.1.0
     */
    @Contract("_, !null -> !null")
    fun getString(path: String, def: String? = null): String?

    /**
     * Retrieves a boolean value.
     *
     * @param path dot-separated configuration path
     * @param def value to return when the path has no usable value
     * @return the configured boolean, or [def] when unavailable
     * @since 0.1.0
     */
    @Contract("_, !null -> !null")
    fun getBoolean(path: String, def: Boolean? = null): Boolean?

    /**
     * Retrieves an integer value.
     *
     * @param path dot-separated configuration path
     * @param def value to return when the path has no usable value
     * @return the configured integer, or [def] when unavailable
     * @since 0.1.0
     */
    @Contract("_, !null -> !null")
    fun getInt(path: String, def: Int? = null): Int?

    /**
     * Retrieves a long value.
     *
     * @param path dot-separated configuration path
     * @param def value to return when the path has no usable value
     * @return the configured long, or [def] when unavailable
     * @since 0.1.0
     */
    @Contract("_, !null -> !null")
    fun getLong(path: String, def: Long? = null): Long?

    /**
     * Retrieves a double value.
     *
     * @param path dot-separated configuration path
     * @param def value to return when the path has no usable value
     * @return the configured double, or [def] when unavailable
     * @since 0.1.0
     */
    @Contract("_, !null -> !null")
    fun getDouble(path: String, def: Double? = null): Double?

    /**
     * Retrieves a list value.
     *
     * @param path dot-separated configuration path
     * @return the configured list, or an empty list when no list exists
     * @since 0.1.0
     */
    fun getList(path: String): List<Any?>

    /**
     * Retrieves a list value with each element represented as a string.
     *
     * @param path dot-separated configuration path
     * @return the configured values converted to strings
     * @since 0.1.0
     */
    fun getAsStringList(path: String): List<String>

    /**
     * Sets a value at the specified path.
     *
     * The change is kept in memory and marks the configuration as dirty
     * until it is successfully saved.
     *
     * @param path dot-separated configuration path
     * @param value value to store
     * @since 0.1.0
     */
    fun set(path: String, value: Any?)

    /**
     * Removes the value at the specified path.
     *
     * @param path dot-separated configuration path
     * @return `true` if a value was removed, or `false` if the path
     * did not contain a value
     * @since 0.1.0
     */
    fun remove(path: String): Boolean

    /**
     * Retrieves the comment associated with a configuration path.
     *
     * @param path dot-separated configuration path
     * @return the configured comment, or `null` when no comment exists
     * @since 0.1.0
     */
    fun getComment(path: String): String?

    /**
     * Sets or removes the comment associated with a configuration path.
     *
     * Passing `null` removes the existing comment.
     *
     * Whether comments are supported depends on the configuration format.
     * See [ConfigFormat.supportsComments].
     *
     * @param path dot-separated configuration path
     * @param comment comment to associate with the path, or `null` to remove it
     * @throws UnsupportedOperationException if the configuration format does
     * not support comments
     * @since 0.1.0
     */
    fun setComment(path: String, comment: String?)

    /**
     * Persists the current in-memory configuration to disk.
     *
     * After a successful save, [isDirty] becomes `false`.
     *
     * Implementation should avoid rewriting an unchanged configuration.
     *
     * @throws ConfigException if the configuration cannot be saved
     * @since 0.1.0
     */
    fun save()

    /**
     * Reloads the configuration from its backing file.
     *
     * Any unsaved in-memory changes are discarded.
     *
     * @throws ConfigException if the configuration cannot be reloaded
     * @since 0.1.0
     */
    fun reload()

    /**
     * Releases resources associated with this configuration.
     *
     * Once closed, the configuration should no longer be used.
     *
     * @since 0.1.0
     */
    override fun close()

}