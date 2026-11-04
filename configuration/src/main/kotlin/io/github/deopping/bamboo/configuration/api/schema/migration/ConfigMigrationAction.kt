package io.github.deopping.bamboo.configuration.api.schema.migration

/**
 * Represents an operation that migrates a configuration between versions.
 *
 * A migration action receives a [ConfigMigrationScope] that provides access
 * to the configuration being migrated and convenience operations for
 * modifying its values.
 *
 * Migration actions are executed by the configuration updater when a
 * configuration's stored version is older than the target schema version
 * and a matching migration is available.
 *
 * This is a functional interface and can be implemented using a lambda.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public fun interface ConfigMigrationAction {
    /**
     * Applies this migration to a configuration.
     *
     * Changes made through the supplied scope modify the configuration
     * being migrated. If the action fails, the configuration update
     * is aborted.
     *
     * @param scope the scope used to access and modify the configuration
     * @throws Exception if the migration cannot be completed
     * @since 1.0.0
     */
    public fun apply(scope: ConfigMigrationScope)
}
