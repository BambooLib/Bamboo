package io.github.deopping.bamboo.database.api.sql

import io.github.deopping.bamboo.database.api.Database
import javax.sql.DataSource

/**
 * Represents a relational database accessed through JDBC.
 *
 * This interface is shared by SQLite, H2, MySQL, and MariaDB databases.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface SqlDatabase : Database {

    /**
     * The JDBC datasource used by this database.
     * @since 0.1.0
     */
    val dataSource: DataSource

    /**
     * Executes an SQL statement.
     *
     * This method is intended primarily for schema and migration operations.
     *
     * @param sql SQL statement to execute
     * @return number of affected rows when provided by the JDBC driver
     * @since 0.1.0
     */
    fun execute(sql: String): Int

    /**
     * Executes an operation inside a JDBC transaction.
     *
     * @param action transaction callback
     * @return value returned by the transaction
     * @since 0.1.0
     */
    fun <T> transaction(
        action: SqlTransactionAction<T>
    ): T

}