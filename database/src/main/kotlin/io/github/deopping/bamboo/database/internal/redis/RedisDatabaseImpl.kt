package io.github.deopping.bamboo.database.internal.redis

import io.github.deopping.bamboo.database.api.DatabaseState
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.redis.RedisDatabase
import io.github.deopping.bamboo.database.internal.DatabaseFailures
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class RedisDatabaseImpl(
    override val name: String,
    override val client: RedisClient,
    override val connection: StatefulRedisConnection<String, String>
) : RedisDatabase {

    override val type: DatabaseType = DatabaseType.REDIS

    override val commands: RedisCommands<String, String>
        get() = connection.sync()

    private val lifecycleLock = ReentrantReadWriteLock()

    @Volatile
    private var currentState = DatabaseState.CONNECTING

    override val state: DatabaseState
        get() = currentState

    internal fun initialize() {
        lifecycleLock.write {
            check(currentState == DatabaseState.CONNECTING) {
                "Redis database '$name' has already been initialized."
            }

            try {
                check(connection.sync().ping() == "PONG") {
                    "Redis returned an unexpected response to PING."
                }

                currentState = DatabaseState.CONNECTED
            } catch (failure: Exception) {
                currentState = DatabaseState.FAILED

                throw DatabaseFailures.create(
                    "Failed to initialize Redis database '$name'.",
                    failure
                )
            }
        }
    }

    override fun ping(): Boolean = lifecycleLock.read {
        if (currentState != DatabaseState.CONNECTED) {
            return@read false
        }

        try {
            connection.sync().ping() == "PONG"
        } catch (_: Exception) {
            false
        }
    }

    override fun close() {
        lifecycleLock.write {
            if (currentState == DatabaseState.CLOSED) {
                return
            }

            currentState = DatabaseState.CLOSING

            var failure: Exception? = null

            try {
                connection.close()
            } catch (exception: Exception) {
                failure = exception
            }

            try {
                client.shutdown()
            } catch (exception: Exception) {
                val previousFailure = failure

                if (previousFailure == null) {
                    failure = exception
                } else {
                    previousFailure.addSuppressed(exception)
                }
            }

            if (failure != null) {
                currentState = DatabaseState.FAILED

                throw DatabaseFailures.create(
                    "Failed to close Redis database '$name'.",
                    failure
                )
            }

            currentState = DatabaseState.CLOSED
        }
    }

}