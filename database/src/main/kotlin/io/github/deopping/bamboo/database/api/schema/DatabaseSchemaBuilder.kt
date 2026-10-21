package io.github.deopping.bamboo.database.api.schema

import io.github.deopping.bamboo.database.DatabaseApi
import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.migration.DatabaseMigrationAction

/**
 * Builds a [DatabaseSchema].
 *
 * Implementations are supplied by Bamboo and should normally be obtained
 * through [DatabaseApi].
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseSchemaBuilder {

    /**
     * Sets the current schema version.
     *
     * @param version current schema version
     * @since 0.1.0
     */
    fun version(version: Int)

    /**
     * Adds a migration to the schema.
     *
     * Each source version may have at most one migration. The migration
     * target must be greater than its source and must not exceed the current
     * schema version.
     *
     * This method is the Java-friendly form of the Kotlin [migrate] DSL.
     *
     * @param from source schema version
     * @param to target schema version
     * @param action migration action
     * @since 0.1.0
     */
    fun migration(
        from: Int,
        to: Int,
        action: DatabaseMigrationAction
    )

    /**
     * Builds the final immutable schema.
     *
     * @return constructed database schema
     * @throws IllegalStateException if the schema definition is invalid
     * @since 0.1.0
     */
    fun build(): DatabaseSchema

}

/**
 * Adds a migration using a Kotlin DSL.
 *
 * @param from source schema version
 * @param to target schema version
 * @param configure migration configuration
 */
fun DatabaseSchemaBuilder.migrate(
    from: Int,
    to: Int,
    configure: Database.() -> Unit
) {
    migration(
        from = from,
        to = to,
        action = { database -> database.configure() }
    )
}