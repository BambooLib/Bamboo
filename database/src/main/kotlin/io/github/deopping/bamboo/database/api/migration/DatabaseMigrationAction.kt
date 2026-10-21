package io.github.deopping.bamboo.database.api.migration

import io.github.deopping.bamboo.database.api.Database

/**
 * Performs a database schema migration.
 *
 * The action is invoked only when the database must advance from the
 * migration's source version to its target version.
 *
 * For SQL migrations, Bamboo supplies a database view scoped to the
 * migration transaction. Use its SQL operations rather than obtaining
 * independent connections through the datasource when modifying the schema.
 *
 * @author DeOpping
 * @since 0.1.0
 */
fun interface DatabaseMigrationAction {

    /**
     * Applies the migration.
     *
     * @param database database being migrated
     * @throws Exception if the migration fails
     * @since 0.1.0
     */
    fun migrate(database: Database)

}