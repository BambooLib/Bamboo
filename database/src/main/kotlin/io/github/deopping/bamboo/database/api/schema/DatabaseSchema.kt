package io.github.deopping.bamboo.database.api.schema

import io.github.deopping.bamboo.database.api.migration.DatabaseMigration

/**
 * Describes the schema version and migrations for a database.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseSchema {

    /**
     * The current schema version.
     * @since 0.1.0
     */
    val version: Int

    /**
     * The migrations defined by this schema.
     * @since 0.1.0
     */
    val migrations: Map<Int, DatabaseMigration>

}