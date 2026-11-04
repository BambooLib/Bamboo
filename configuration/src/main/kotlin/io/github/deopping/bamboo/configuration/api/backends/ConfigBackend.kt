package io.github.deopping.bamboo.configuration.api.backends

import java.lang.AutoCloseable
import java.util.Optional

/**
 * Provides low-level access to a configuration file.
 *
 * A configuration backend is responsible for loading, storing, and managing
 * configuration data in a particular file format.
 *
 * Backends are created through a [ConfigBackendFactory] and managed by
 * Bamboo's configuration implementation. They provide the underlying
 * storage operations used by [io.github.deopping.bamboo.configuration.api.Config].
 *
 * Configuration paths are represented as lists of path components rather than
 * dot-separated strings. Each component identifies a successive level within
 * the configuration structure.
 *
 * A backend must be loaded before its configuration data is accessed.
 * Once closed, it must not be used again.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigBackend : AutoCloseable {
    /**
     * Loads configuration data from the backing file.
     *
     * This operation initializes or refreshes the backend's in-memory
     * representation of the configuration.
     *
     * @throws Exception if the configuration data cannot be loaded
     * @since 1.0.0
     */
    public fun load()

    /**
     * Saves the current configuration data to the backing file.
     *
     * @throws Exception if the configuration data cannot be saved
     * @since 1.0.0
     */
    public fun save()

    /**
     * Checks whether a value exists at the specified configuration path.
     *
     * @param path the configuration path represented as individual path components
     * @return `true` if a value exists at the path, otherwise `false`
     * @since 1.0.0
     */
    public fun contains(path: List<String>): Boolean

    /**
     * Gets a value at the specified configuration path.
     *
     * The returned value is not converted to a particular type.
     *
     * @param path the configuration path represented as individual path components
     * @return the value, or an empty optional if no value exists at the path
     * @since 1.0.0
     */
    public fun get(path: List<String>): Optional<Any>

    /**
     * Gets the configuration section at the specified path.
     *
     * The returned map contains the direct entries of the requested section.
     * Nested sections should be represented as nested maps.
     *
     * @param path the configuration path represented as individual path components
     * @return the section contents, or an empty optional if no section exists at the path
     * @since 1.0.0
     */
    public fun getSection(path: List<String>): Optional<Map<String, Any?>>

    /**
     * Sets a value at the specified configuration path.
     *
     * If a value already exists at the path, it is replaced.
     *
     * @param path the configuration path represented as individual path components
     * @param value the value to store
     * @since 1.0.0
     */
    public fun set(
        path: List<String>,
        value: Any,
    )

    /**
     * Removes the value at the specified configuration path.
     *
     * @param path the configuration path represented as individual path components
     * @return `true` if a value was removed, otherwise `false`
     * @since 1.0.0
     */
    public fun remove(path: List<String>): Boolean

    /**
     * Gets the comment associated with the specified configuration path.
     *
     * Backends that do not support comments should return an empty optional.
     *
     * @param path the configuration path represented as individual path components
     * @return the comment, or an empty optional if no comment exists or comments
     * are not supported
     * @since 1.0.0
     */
    public fun getComment(path: List<String>): Optional<String>

    /**
     * Sets the comment associated with the specified configuration path.
     *
     * The backend must support comments for this operation to succeed.
     *
     * @param path the configuration path represented as individual path components
     * @param comment the comment to associate with the path
     * @throws UnsupportedOperationException if the backend does not support comments
     * @since 1.0.0
     */
    public fun setComment(
        path: List<String>,
        comment: String,
    )

    /**
     * Removes the comment associated with the specified configuration path.
     *
     * Backends that do not support comments should return `false`.
     *
     * @param path the configuration path represented as individual path components
     * @return `true` if a comment was removed, otherwise `false`
     * @since 1.0.0
     */
    public fun removeComment(path: List<String>): Boolean

    /**
     * Closes the backend and releases any resources it owns.
     *
     * Once closed, the backend must not be used again.
     *
     * @since 1.0.0
     */
    override fun close()
}
