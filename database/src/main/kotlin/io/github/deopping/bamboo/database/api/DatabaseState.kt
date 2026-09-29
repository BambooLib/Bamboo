package io.github.deopping.bamboo.database.api

/**
 * Represents the lifecycle state of a database connection.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class DatabaseState {

    /**
     * The database has not been connected.
     * @since 0.1.0
     */
    DISCONNECTED,

    /**
     * The database is currently connecting.
     * @since 0.1.0
     */
    CONNECTING,

    /**
     * The database is connected and available.
     * @since 0.1.0
     */
    CONNECTED,

    /**
     * The database is currently being closed.
     * @since 0.1.0
     */
    CLOSING,

    /**
     * The database has been closed.
     * @since 0.1.0
     */
    CLOSED,

    /**
     * The database failed to connect or encountered a fatal connection error.
     * @since 0.1.0
     */
    FAILED

}