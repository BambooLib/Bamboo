package io.github.deopping.bamboo.database.internal.sql

import io.github.deopping.bamboo.database.api.sql.SqlDatabase
import io.github.deopping.bamboo.database.api.sql.SqlTransactionAction
import java.sql.Connection

internal class MigrationScopedSqlDatabase(
    private val delegate: SqlDatabase,
    private val connection: Connection
) : SqlDatabase by delegate {

    override fun execute(sql: String): Int {
        require(sql.isNotBlank()) {
            "SQL statement must not be blank."
        }

        return connection.createStatement().use { statement ->
            statement.executeUpdate(sql)
        }
    }

    override fun <T> transaction(action: SqlTransactionAction<T>): T {
        val savepoint = connection.setSavepoint()

        try {
            val result = action.execute(connection)
            connection.releaseSavepoint(savepoint)
            return result
        } catch (failure: Throwable) {
            try {
                connection.rollback(savepoint)
            } catch (rollbackFailure: Throwable) {
                failure.addSuppressed(rollbackFailure)
            }

            throw failure
        }
    }

    override fun ping(): Boolean =
        try {
            !connection.isClosed && connection.isValid(5)
        } catch (_: Exception) {
            false
        }

    override fun close() {
        throw IllegalStateException(
            "A migration action must not close its managed database."
        )
    }

}