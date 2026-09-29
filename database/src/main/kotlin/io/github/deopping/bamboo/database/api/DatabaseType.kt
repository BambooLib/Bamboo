package io.github.deopping.bamboo.database.api

/**
 * Identifies a database backend supported by Bamboo.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class DatabaseType(
    val family: DatabaseFamily
) {

    /**
     * SQLite embedded relational database.
     * @since 0.1.0
     */
    SQLITE(DatabaseFamily.SQL),

    /**
     * H2 relational database.
     * @since 0.1.0
     */
    H2(DatabaseFamily.SQL),

    /**
     * MySQL relational database.
     * @since 0.1.0
     */
    MYSQL(DatabaseFamily.SQL),

    /**
     * MariaDB relational database.
     * @since 0.1.0
     */
    MARIADB(DatabaseFamily.SQL),

    /**
     * MongoDB document database.
     * @since 0.1.0
     */
    MONGODB(DatabaseFamily.DOCUMENT),

    /**
     * Redis key-value database.
     * @since 0.1.0
     */
    REDIS(DatabaseFamily.KEY_VALUE)

}