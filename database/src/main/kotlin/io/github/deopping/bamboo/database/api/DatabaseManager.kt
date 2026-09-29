package io.github.deopping.bamboo.database.api

import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import java.lang.AutoCloseable

/**
 * Manages Bamboo database connections.
 *
 * A database manager owns all databases created through it. Database names
 * identify logical databases within the manager and allow multiple plugins
 * or systems to share a common database service without creating duplicate
 * connections.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseManager : AutoCloseable {

    /**
     * Opens and manages a database.
     *
     * If a database with the supplied name is already managed, the existing
     * database is returned and the supplied configuration is ignored.
     *
     * @param name logical database name
     * @param configuration database connection configuration
     * @param schema optional database schema and migration definition
     * @return the managed database
     * @throws DatabaseException if the database cannot be created or connected
     * @since 0.1.0
     */
    fun connect(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database

    /**
     * Returns a managed database without creating a new connection.
     *
     * @param name logical database name
     * @return managed database, or `null` when none exists
     * @since 0.1.0
     */
    fun get(name: String): Database?

    /**
     * Closes and removes a managed database.
     *
     * @param name logical database name
     * @since 0.1.0
     */
    fun disconnect(name: String)

    /**
     * Closes all managed databases.
     *
     * @since 0.1.0
     */
    override fun close()

}