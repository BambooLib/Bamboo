package io.github.deopping.bamboo.database.api

import java.lang.AutoCloseable

/**
 * Represents a managed Bamboo database.
 *
 * A database owns its underlying connection resources and provides common
 * lifecycle and health operations independent of the specific database
 * implementation.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface Database : AutoCloseable {

    /**
     * The logical name assigned to this database.
     * @since 0.1.0
     */
    val name: String

    /**
     * The database backend type.
     * @since 0.1.0
     */
    val type: DatabaseType

    /**
     * The current lifecycle state of this database.
     * @since 0.1.0
     */
    val state: DatabaseState

    /**
     * Checks whether the database is currently reachable.
     *
     * @return `true` when the database is reachable
     * @since 0.1.0
     */
    fun ping(): Boolean

    /**
     * Closes the database and releases its resources.
     *
     * @since 0.1.0
     */
    override fun close()

}