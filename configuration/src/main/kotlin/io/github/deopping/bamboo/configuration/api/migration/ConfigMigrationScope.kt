package io.github.deopping.bamboo.configuration.api.migration

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigException

/**
 * Provides operations for modifying a configuration during a migration.
 *
 * A migration scope is only valid when its associated migration is being executed.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigMigrationScope {

    /**
     * The configuration currently being migrated.
     * @since 0.1.0
     */
    val config: Config

    /**
     * Sets a configuration value.
     *
     * @param path configuration path
     * @param value value to assign
     * @since 0.1.0
     */
    fun set(path: String, value: Any?)

    /**
     * Removes a configuration value.
     *
     * @Param path configuration path
     * @return `true` when a value was removed, `false` otherwise
     * @since 0.1.0
     */
    fun remove(path: String): Boolean

    /**
     * Renames a configuration value.
     *
     * Existing comments associated with the source path may also be moved
     * when the configuration format supports comments.
     *
     * @param from source configuration path
     * @param to destination configuration path
     * @param overwrite whether an existing destination value may be replaced
     * @throws ConfigException if the destination exists and overwriting is disabled
     * @since 0.1.0
     */
    fun rename(
        from: String,
        to: String,
        overwrite: Boolean = false
    )

    /**
     * Copies a configuration value.
     *
     * @param from source configuration path
     * @param to destination configuration path
     * @param overwrite whether an existing destination value may be replaced
     * @throws ConfigException if the destination exists and overwriting is disabled
     * @since 0.1.0
     */
    fun copy(
        from: String,
        to: String,
        overwrite: Boolean = false
    )

}