package io.github.deopping.bamboo.database.api.sql

import java.sql.Connection

/**
 * Performs work using a JDBC transaction.
 *
 * @param T result type
 * @author DeOpping
 * @since 0.1.0
 */
interface SqlTransactionAction<T> {

    /**
     * Executes the transaction.
     *
     * @param connection JDBC connection participating in the transaction
     * @return transaction result
     * @throws Exception if the operation fails
     * @since 0.1.0
     */
    fun execute(connection: Connection): T

}