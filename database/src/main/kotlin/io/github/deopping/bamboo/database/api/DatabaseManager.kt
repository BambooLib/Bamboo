package io.github.deopping.bamboo.database.api

import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import java.lang.AutoCloseable

/**
 * Manages Bamboo database connections and their associated resources.
 *
 * A database manager owns the database it creates. Database names identify
 * logical databases within the manager, allowing multiple components to share
 * a managed database instead of opening duplicate connections.
 *
 * Closing the manager closes all databases it manages and releases their
 * associated resources.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface DatabaseManager : AutoCloseable {

    /**
     * Opens or retrieves a managed database.
     *
     * The database configuration determines the backend and connection
     * settings. When a schema is provided, its migrations are applied as
     * part of database initialization.
     *
     * If a database with the specified name is already managed, its
     * existing instance is returned. The supplied configuration and schema
     * are not applied to the existing instance.
     *
     * @param name logical name used to identify the managed database
     * @param configuration configuration describing the database connection
     * @param schema optional schema definition and migration history
     * @return the managed database
     * @throws IllegalArgumentException if the name is invalid
     * @throws DatabaseException if the database cannot be created, connected,
     * or initialized
     * @since 0.1.0
     */
    fun connect(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema? = null
    ): Database

    /**
     * Retrieves a database managed under the specified name.
     *
     * This method does not create a connection or attempt to reconnect
     * a database.
     *
     * @param name logical name of the database
     * @return the managed database, or `null` if no database is managed
     * under that name
     * @since 0.1.0
     */
    fun get(name: String): Database?

    /**
     * Closes and removes a database from this manager.
     *
     * If no database is managed under the specified name, this method
     * has no effect.
     *
     * @param name logical name of the database to disconnect
     * @throws DatabaseException if the database cannot be closed cleanly
     * @since 0.1.0
     */
    fun disconnect(name: String)

    /**
     * Closes all managed databases and releases their resources.
     *
     * Once closed, the manager must not accept new connections. Repeated
     * calls should have no additional effect.
     *
     * @throws DatabaseException if one or more databases cannot be closed
     * @since 0.1.0
     */
    override fun close()

}