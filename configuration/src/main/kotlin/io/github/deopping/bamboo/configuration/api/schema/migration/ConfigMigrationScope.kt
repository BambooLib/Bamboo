package io.github.deopping.bamboo.configuration.api.schema.migration

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException

/**
 * Provides access to a configuration during a migration.
 *
 * A migration scope exposes the configuration being migrated and provides
 * convenience operations for setting, removing, renaming, and copying
 * configuration values.
 *
 * The scope is created by the configuration updater for the duration
 * of a migration action.
 *
 * All modifications are applied directly to the underlying configuration
 * and are tracked as unsaved changes.
 *
 * @property config the configuration currently being migrated
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigMigrationScope {
    /**
     * Gets the configuration being migrated.
     *
     * This is the live configuration instance. Changes made through it
     * are reflected in the configuration being migrated.
     *
     * @return the configuration being migrated
     * @since 1.0.0
     */
    public val config: Config

    /**
     * Sets a configuration value.
     *
     * If a value already exists at the specified path, it is replaced.
     *
     * @param path the configuration path
     * @param value the value to store
     * @throws IllegalArgumentException if the configuration path is invalid
     * @throws ConfigException if the value cannot be written
     * @since 1.0.0
     */
    public fun set(
        path: String,
        value: Any,
    )

    /**
     * Removes a configuration value.
     *
     * If no value exists at the specified path, no modification is made.
     *
     * @param path the configuration path
     * @return `true` if a value was removed, otherwise `false`
     * @throws IllegalArgumentException if the configuration path is invalid
     * @throws ConfigException if the value cannot be removed
     * @since 1.0.0
     */
    public fun remove(path: String): Boolean

    /**
     * Renames a configuration value to another path.
     *
     * If the source path does not contain a value, this operation has
     * no effect.
     *
     * If the destination already contains a value and [overwrite] is `false`,
     * the operation fails. When [overwrite] is `true`, the destination value
     * is replaced.
     *
     * When the configuration format supports comments, an existing source
     * comment is copied to the destination if the destination does not
     * already have a comment.
     *
     * The source value is removed after the destination value has been
     * written successfully.
     *
     * @param from the source configuration path
     * @param to the destination configuration path
     * @param overwrite whether an existing destination value may be replaced
     * @throws IllegalArgumentException if either configuration path is invalid
     * @throws ConfigException if the destination already exists and overwrite is disabled,
     * or if the value cannot be moved
     * @since 1.0.0
     */
    public fun rename(
        from: String,
        to: String,
        overwrite: Boolean,
    )

    /**
     * Copies a configuration value to another path.
     *
     * If the source path does not contain a value, this operation has
     * no effect.
     *
     * If the destination already contains a value and [overwrite] is `false`,
     * the operation fails. When [overwrite] is `true`, the destination value
     * is replaced.
     *
     * The source value remains unchanged. Comments associated with the
     * source path are not copied.
     *
     * @param from the source configuration path
     * @param to the destination configuration path
     * @param overwrite whether an existing destination value may be replaced
     * @throws IllegalArgumentException if either configuration path is invalid
     * @throws ConfigException if the destination already exists and overwrite is disabled,
     * or if the value cannot be copied
     * @since 1.0.0
     */
    public fun copy(
        from: String,
        to: String,
        overwrite: Boolean,
    )
}
