package io.github.deopping.bamboo.database.internal

import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseException
import io.github.deopping.bamboo.database.api.DatabaseManager
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class DatabaseManagerImpl(
    private val factory: DatabaseFactory
) : DatabaseManager {

    private val lock = ReentrantLock()
    private val closeLock = ReentrantLock()

    private val databases = LinkedHashMap<String, Database>()
    private val nameLocks = LinkedHashMap<String, ReentrantLock>()

    private var closed = false

    override fun connect(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database {
        requireName(name)

        val databaseLock = lockFor(name)

        return databaseLock.withLock {
            val existing = lock.withLock {
                check(!closed) {
                    "Database manager is closed."
                }

                databases[name]
            }

            if (existing != null) {
                return@withLock existing
            }

            val database = createDatabase(
                name = name,
                configuration = configuration,
                schema = schema
            )

            val registered = lock.withLock {
                if (closed) {
                    false
                } else {
                    databases[name] = database
                    true
                }
            }

            if (!registered) {
                val failure = DatabaseException(
                    "Database manager was closed while " +
                            "database '$name' was being initialized."
                )

                closeSuppressing(database, failure)
                throw failure
            }

            database
        }
    }

    override fun get(name: String): Database? {
        requireName(name)

        return lock.withLock {
            databases[name]
        }
    }

    override fun disconnect(name: String) {
        requireName(name)

        val databaseLock = lock.withLock {
            if (closed) {
                return@withLock null
            }

            nameLocks[name]
        } ?: return

        databaseLock.withLock {
            val database = lock.withLock {
                if (closed) {
                    return@withLock null
                }

                databases.remove(name)
            }

            if (database != null) {
                closeDatabase(name, database)
            }
        }
    }

    override fun close() {
        closeLock.withLock {
            val locksToAcquire = lock.withLock {
                if (closed) {
                    return
                }

                closed = true
                nameLocks.values.toList()
            }

            val acquiredLocks = mutableListOf<ReentrantLock>()

            try {
                // Wait for ongoing connections and disconnections to finish
                // No new named operations can begin after closed becomes true.
                for (databaseLock in locksToAcquire) {
                    databaseLock.lock()
                    acquiredLocks.add(databaseLock)
                }

                val managedDatabases = lock.withLock {
                    databases.entries
                        .map { (name, database) -> name to database }
                        .also { databases.clear() }
                }

                val failures = mutableListOf<DatabaseException>()

                for ((name, database) in managedDatabases) {
                    try {
                        closeDatabase(name, database)
                    } catch (failure: DatabaseException) {
                        failures.add(failure)
                    }
                }

                if (failures.isNotEmpty()) {
                    val exception = DatabaseException(
                        "Failed to close ${failures.size} of " +
                        "${managedDatabases.size} managed database(s)."
                    )

                    failures.drop(1).forEach(exception::addSuppressed)
                    throw exception
                }
            } finally {
                acquiredLocks.asReversed().forEach { it.unlock() }
            }
        }
    }

    private fun createDatabase(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database {
        val database = try {
            factory.create(
                name = name,
                configuration = configuration,
                schema = schema
            )
        } catch (failure: DatabaseException) {
            throw failure
        } catch (failure: Exception) {
            throw DatabaseException(
                "Failed to initialize database '$name'.",
                failure
            )
        }

        if (database.name != name || database.type != configuration.type) {
            val failure = DatabaseException(
                "Database factory returned an instance whose name or " +
                "type does not match the requested database '$name'."
            )

            closeSuppressing(database, failure)
            throw failure
        }

        return database
    }

    private fun closeDatabase(
        name: String,
        database: Database
    ) {
        try {
            database.close()
        } catch (failure: Exception) {
            throw DatabaseException(
                "Failed to close database '$name'.",
                failure
            )
        }
    }

    private fun closeSuppressing(
        database: Database,
        failure: Throwable
    ) {
        try {
            database.close()
        } catch (closeFailure: Exception) {
            failure.addSuppressed(closeFailure)
        }
    }

    private fun lockFor(name: String): ReentrantLock =
        lock.withLock {
            check(!closed) {
                "Database manager is closed."
            }

            nameLocks.getOrPut(name) {
                ReentrantLock()
            }
        }

    private fun requireName(name: String) {
        require(name.isNotBlank()) {
            "Database name must not be blank."
        }
    }

}