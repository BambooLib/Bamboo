package io.github.deopping.bamboo.database

import io.github.deopping.bamboo.database.api.DatabaseManager

/**
 * Entry point for Bamboo's database system.
 *
 * Platform integrations provide the implementation of this interface.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseApi {

    /**
     * Creates a new database manager.
     *
     * @return a new [DatabaseManager]
     * @since 0.1.0
     */
    fun createDatabaseManager(): DatabaseManager

//    /**
//     * Creates a new database schema builder.
//     *
//     * @return a new [DatabaseSchemaBuilder]
//     * @since 0.1.0
//     */
//    fun createDatabaseSchemaBuilder(): DatabaseSchemaBuilder

}