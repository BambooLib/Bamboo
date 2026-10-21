package io.github.deopping.bamboo.database.internal.sql

import com.zaxxer.hikari.HikariDataSource
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseState
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.sql.SqlDatabase
import io.github.deopping.bamboo.database.api.sql.SqlTransactionAction
import io.github.deopping.bamboo.database.internal.DatabaseFailures
import java.sql.SQLException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class SqlDatabaseImpl(
    override val name: String,
    override val type: DatabaseType,
    override val dataSource: HikariDataSource
) : SqlDatabase {

    private val lifecycleLock = ReentrantReadWriteLock()

    @Volatile
    private var currentState = DatabaseState.CONNECTING

    override val state: DatabaseState
        get() = currentState

    init {
        require(type.family == DatabaseFamily.SQL) {
            "SqlDatabaseImpl requires an SQL database type."
        }
    }

    /**
     * Verifies that the datasource can establish a usable connection.
     */
    internal fun initialize() {
        lifecycleLock.write {
            check(currentState == DatabaseState.CONNECTING) {
                "Database '$name' has already been initialized."
            }

            try {
                dataSource.connection.use { connection ->
                    if (!connection.isValid(5)) {
                        throw SQLException(
                            "The database connection failed validation."
                        )
                    }
                }

                currentState = DatabaseState.CONNECTED
            } catch (failure: Throwable) {
                currentState = DatabaseState.FAILED

                try {
                    dataSource.close()
                } catch (closeFailure: Throwable) {
                    failure.addSuppressed(closeFailure)
                }

                if (failure is Error) {
                    throw failure
                }

                if (failure is DatabaseException) {
                    throw failure
                }

                throw DatabaseFailures.create(
                    "Failed to initialize SQL database '$name'.",
                    failure
                )
            }
        }
    }

    override fun ping(): Boolean =
        lifecycleLock.read {
            if (currentState != DatabaseState.CONNECTED) {
                return@read false
            }

            try {
                dataSource.connection.use { connection ->
                    connection.isValid(5)
                }
            } catch (_: Exception) {
                false
            }
        }

    override fun execute(sql: String): Int {
        require(sql.isNotBlank()) {
            "SQL statement must not be blank."
        }

        return withConnected("execute SQL") {
            dataSource.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate(sql)
                }
            }
        }
    }

    override fun <T> transaction(
        action: SqlTransactionAction<T>
    ): T = withConnected("execute transaction") {
        dataSource.connection.use { connection ->
            val originalAutoCommit = connection.autoCommit
            var transactionFailure: Throwable? = null

            try {
                connection.autoCommit = false

                val result = action.execute(connection)

                connection.commit()

                result
            } catch (failure: Throwable) {
                transactionFailure = failure

                try {
                    connection.rollback()
                } catch (rollbackFailure: Throwable) {
                    failure.addSuppressed(rollbackFailure)
                }

                throw failure
            } finally {
                try {
                    if (!connection.isClosed && connection.autoCommit != originalAutoCommit) {
                        connection.autoCommit = originalAutoCommit
                    }
                } catch (restoreFailure: Throwable) {
                    val failure = transactionFailure

                    if (failure != null) {
                        failure.addSuppressed(restoreFailure)
                    } else {
                        throw restoreFailure
                    }
                }
            }
        }
    }

    override fun close() {
        lifecycleLock.write {
            if (currentState == DatabaseState.CLOSED) {
                return
            }

            currentState = DatabaseState.CLOSING

            try {
                dataSource.close()
                currentState = DatabaseState.CLOSED
            } catch (failure: Exception) {
                currentState = DatabaseState.FAILED

                throw DatabaseFailures.create(
                    "Failed to close SQL database '$name'.",
                    failure
                )
            }
        }
    }

    private inline fun <T> withConnected(
        operation: String,
        action: () -> T
    ): T = lifecycleLock.read {
        if (currentState != DatabaseState.CONNECTED) {
            throw DatabaseFailures.create(
                "Cannot $operation on database '$name': " +
                "its current state is $currentState."
            )
        }

        try {
            action()
        } catch (failure: DatabaseException) {
            throw failure
        } catch (failure: Exception) {
            throw DatabaseFailures.create(
                "Failed to $operation on SQL database '$name'.",
                failure
            )
        }
    }

}