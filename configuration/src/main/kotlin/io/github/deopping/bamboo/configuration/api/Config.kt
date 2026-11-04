package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigValueException
import java.lang.AutoCloseable
import java.nio.file.Path
import java.util.Optional

/**
 * Represents a loaded configuration.
 *
 * A configuration provides access to stored values, sections, and comments,
 * as well as operations for modifying, saving, and reloading its contents.
 *
 * Changes made through this interface are tracked until they are saved or
 * discarded through a reload operation.
 *
 * Configurations are managed by a [ConfigManager] and should be closed when
 * they are no longer needed.
 *
 * @property path the normalized file path associated with this configuration
 * @property format the format used to read and write this configuration
 * @property isDirty whether the configuration contains unsaved changes
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface Config : AutoCloseable {
    /**
     * Gets the file path associated with this configuration.
     *
     * @return the configuration file path
     * @since 1.0.0
     */
    public val path: Path

    /**
     * Gets the format used by this configuration.
     *
     * @return the configuration format
     * @since 1.0.0
     */
    public val format: ConfigFormat

    /**
     * Checks whether this configuration contains unsaved changes.
     *
     * A configuration becomes dirty when a modification changes its
     * in-memory contents. Successfully saving or reloading the configuration
     * clears this state.
     *
     * @return `true` if the configuration contains unsaved changes
     * @since 1.0.0
     */
    public val isDirty: Boolean

    /**
     * Checks whether a value exists at the specified configuration path.
     *
     * @param path the configuration path
     * @return `true` if a value exists at the path, otherwise `false`
     * @throws ConfigException if the path is invalid or the configuration cannot be accessed
     * @since 1.0.0
     */
    public fun contains(path: String): Boolean

    /**
     * Gets the value at the specified configuration path.
     *
     * The returned value is not converted to a particular type.
     *
     * @param path the configuration path
     * @return the value, or an empty optional if no value exists at the path
     * @throws ConfigException if the path is invalid or the configuration cannot be accessed
     * @since 1.0.0
     */
    public fun get(path: String): Optional<Any>

    /**
     * Gets a string value at the specified configuration path.
     *
     * @param path the configuration path
     * @return the string value, or an empty optional if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a string
     * @since 1.0.0
     */
    public fun getString(path: String): Optional<String>

    /**
     * Gets a string value at the specified configuration path.
     *
     * The supplied default value is returned when no value exists at the path.
     *
     * @param path the configuration path
     * @param defaultValue the value returned when the configuration value is missing
     * @return the existing string value, or [defaultValue] if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a string
     * @since 1.0.0
     */
    public fun getString(
        path: String,
        defaultValue: String,
    ): String

    /**
     * Gets a boolean value at the specified configuration path.
     *
     * Compatible string representations may also be converted to boolean values.
     *
     * @param path the configuration path
     * @return the boolean value, or an empty optional if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a boolean
     * @since 1.0.0
     */
    public fun getBoolean(path: String): Optional<Boolean>

    /**
     * Gets a boolean value at the specified configuration path.
     *
     * The supplied default value is returned when no value exists at the path.
     *
     * @param path the configuration path
     * @param defaultValue the value returned when the configuration value is missing
     * @return the existing boolean value, or [defaultValue] if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a boolean
     * @since 1.0.0
     */
    public fun getBoolean(
        path: String,
        defaultValue: Boolean,
    ): Boolean

    /**
     * Gets an integer value at the specified configuration path.
     *
     * Compatible integer representations may be converted to an integer value,
     * provided that the value is within the supported integer range.
     *
     * @param path the configuration path
     * @return the integer value, or an empty optional if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as an integer
     * @since 1.0.0
     */
    public fun getInt(path: String): Optional<Int>

    /**
     * Gets an integer value at the specified configuration path.
     *
     * The supplied default value is returned when no value exists at the path.
     *
     * @param path the configuration path
     * @param defaultValue the value returned when the configuration value is missing
     * @return the existing integer value, or [defaultValue] if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as an integer
     * @since 1.0.0
     */
    public fun getInt(
        path: String,
        defaultValue: Int,
    ): Int

    /**
     * Gets a long value at the specified configuration path.
     *
     * Compatible long representations may be converted to a long value,
     * provided that the value is within the supported long range.
     *
     * @param path the configuration path
     * @return the long value, or an empty optional if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a long
     * @since 1.0.0
     */
    public fun getLong(path: String): Optional<Long>

    /**
     * Gets a long value at the specified configuration path.
     *
     * The supplied default value is returned when no value exists at the path.
     *
     * @param path the configuration path
     * @param defaultValue the value returned when the configuration value is missing
     * @return the existing long value, or [defaultValue] if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a long
     * @since 1.0.0
     */
    public fun getLong(
        path: String,
        defaultValue: Long,
    ): Long

    /**
     * Gets a double value at the specified configuration path.
     *
     * Numeric values and compatible string representations may be converted
     * to a double. Non-finite values are not accepted.
     *
     * @param path the configuration path
     * @return the double value, or an empty optional if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a finite double
     * @since 1.0.0
     */
    public fun getDouble(path: String): Optional<Double>

    /**
     * Gets a double value at the specified configuration path.
     *
     * The supplied default value is returned when no value exists at the path.
     *
     * @param path the configuration path
     * @param defaultValue the value returned when the configuration value is missing
     * @return the existing double value, or [defaultValue] if no value exists
     * @throws ConfigException if the existing value cannot be interpreted as a finite double
     * @since 1.0.0
     */
    public fun getDouble(
        path: String,
        defaultValue: Double,
    ): Double

    /**
     * Gets a list value at the specified configuration path.
     *
     * The returned list may contain values of different types and may contain
     * null elements.
     *
     * @param path the configuration path
     * @return the list value, or an empty optional if no value exists
     * @throws ConfigValueException if the existing value is not a list
     * @since 1.0.0
     */
    public fun getList(path: String): Optional<List<Any?>>

    /**
     * Gets a list of string values at the specified configuration path.
     *
     * Every element in the configured list must be a string.
     *
     * @param path the configuration path
     * @return the string list, or an empty optional if no value exists
     * @throws ConfigValueException if the existing value is not a list or contains a non-string element
     * @since 1.0.0
     */
    public fun getAsStringList(path: String): Optional<List<String>>

    /**
     * Gets a configuration section at the specified path.
     *
     * The returned map represents the direct entries of the requested section.
     * Nested sections are represented as nested maps, and nested lists are
     * normalized to ordinary lists.
     *
     * @param path the configuration path
     * @return the section contents, or an empty optional if no value exists at the path
     * @throws ConfigValueException if the existing value is not a configuration section
     * @since 1.0.0
     */
    public fun getSection(path: String): Optional<Map<String, Any?>>

    /**
     * Sets a value at the specified configuration path.
     *
     * If a value already exists at the path, it is replaced.
     *
     * @param path the configuration path
     * @param value the value to store
     * @throws IllegalArgumentException if the path is invalid
     * @throws ConfigException if the value cannot be written
     * @since 1.0.0
     */
    public fun set(
        path: String,
        value: Any,
    )

    /**
     * Removes the value at the specified configuration path.
     *
     * @param path the configuration path
     * @return `true` if a value was removed, otherwise `false`
     * @throws ConfigException if the path is invalid or the value cannot be removed
     * @since 1.0.0
     */
    public fun remove(path: String): Boolean

    /**
     * Gets the comment associated with the specified configuration path.
     *
     * Formats that do not support comments return an empty optional.
     *
     * @param path the configuration path
     * @return the comment, or an empty optional if no comment exists or
     * the format does not support comments
     * @since 1.0.0
     */
    public fun getComment(path: String): Optional<String>

    /**
     * Sets the comment associated with the specified configuration path.
     *
     * The configuration format must support comments.
     *
     * @param path the configuration path
     * @param comment the comment to associate with the path
     * @throws ConfigException if the path is invalid or the comment cannot be written
     * @throws UnsupportedOperationException if the configuration format does not support comments
     * @since 1.0.0
     */
    public fun setComment(
        path: String,
        comment: String,
    )

    /**
     * Removes the comment associated with the specified configuration path.
     *
     * If no comment exists, or the configuration format does not support
     * comments, this operation has no effect.
     *
     * @param path the configuration path
     * @throws ConfigException if the path is invalid or the comment cannot be removed
     * @since 1.0.0
     */
    public fun removeComment(path: String)

    /**
     * Saves the configuration to its backing file.
     *
     * If the configuration has no unsaved changes, this operation has no effect.
     *
     * @throws ConfigException if the configuration cannot be saved
     * @since 1.0.0
     */
    public fun save()

    /**
     * Reloads the configuration from its backing file.
     *
     * Unsaved changes are preserved by default. When [discardUnsavedChanges]
     * is `true`, any unsaved changes are discarded and the configuration is
     * replaced with the contents read from the backing file.
     *
     * @param discardUnsavedChanges whether unsaved changes should be discarded
     * @throws ConfigException if unsaved changes exist and are not allowed to be discarded,
     * or if the configuration cannot be reloaded
     * @since 1.0.0
     */
    public fun reload(discardUnsavedChanges: Boolean = false)

    /**
     * Checks whether the backing file has changed externally and reloads it
     * when appropriate.
     *
     * Configurations containing unsaved local changes are not reloaded.
     *
     * @return `true` if the configuration was reloaded, otherwise `false`
     * @throws ConfigException if the backing file cannot be checked or reloaded
     * @since 1.0.0
     */
    public fun reloadIfChanged(): Boolean

    /**
     * Closes this configuration.
     *
     * Any unsaved changes are saved before the backing resources are released.
     * Once closed, the configuration cannot be used again.
     *
     * @throws ConfigException if pending changes cannot be saved or the configuration
     * cannot be closed
     * @since 1.0.0
     */
    override fun close()
}
