package io.github.deopping.bamboo.configuration.api.migration

/**
 * Represents a migration between two configuration schema versions.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigMigration {

    /**
     * The schema version from which this migration starts.
     * @since 0.1.0
     */
    val from: Int

    /**
     * The schema version produced by this migration.
     * @since 0.1.0
     */
    val to: Int

}