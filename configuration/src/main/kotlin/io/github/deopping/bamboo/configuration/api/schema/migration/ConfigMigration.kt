package io.github.deopping.bamboo.configuration.api.schema.migration

/**
 * Defines a migration between two configuration versions.
 *
 * A migration transforms a configuration from an older version to a newer
 * version by executing its associated [action].
 *
 * Migrations are registered in a [io.github.deopping.bamboo.configuration.api.schema.ConfigSchema]
 * and indexed by their source version. When a configuration is updated,
 * migrations are executed sequentially until the target schema version
 * is reached.
 *
 * @property from the source configuration version
 * @property to the target configuration version
 * @property action the operation performed to migrate the configuration
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigMigration {
    /**
     * Gets the source configuration version.
     *
     * This identifies the configuration version from which this migration
     * can be applied.
     *
     * @return the source configuration version
     * @since 1.0.0
     */
    public val from: Int

    /**
     * Gets the target configuration version.
     *
     * This identifies the configuration version produced by this migration.
     * It must be greater than [from].
     *
     * @return the target configuration version
     * @since 1.0.0
     */
    public val to: Int

    /**
     * Gets the action executed by this migration.
     *
     * @return the migration action
     * @since 1.0.0
     */
    public val action: ConfigMigrationAction
}
