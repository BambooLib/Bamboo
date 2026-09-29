package io.github.deopping.bamboo.database.api.migration

/**
 * Represents a migration from one database schema version to another.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseMigration {

    /**
     * The schema version this migration starts from.
     * @since 0.1.0
     */
    val from: Int

    /**
     * The schema version produced by this migration.
     * @since 0.1.0
     */
    val to: Int

}