package io.github.deopping.bamboo.database.api.migration

import io.github.deopping.bamboo.database.api.Database

/**
 * Performs a database schema migration.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseMigrationAction {

    /**
     * Applies the migration.
     *
     * @param database database being migrated
     * @since 0.1.0
     */
    fun migrate(database: Database)

}