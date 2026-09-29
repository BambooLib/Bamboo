package io.github.deopping.bamboo.database.api

/**
 * Identifies the general data model used by a database.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class DatabaseFamily {

    /**
     * Relational database accessed through SQL/JDBC.
     * @since 0.1.0
     */
    SQL,

    /**
     * Document-oriented database.
     * @since 0.1.0
     */
    DOCUMENT,

    /**
     * Key-value and data-structure database.
     * @since 0.1.0
     */
    KEY_VALUE

}