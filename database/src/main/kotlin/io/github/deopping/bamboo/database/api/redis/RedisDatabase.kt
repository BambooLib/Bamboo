package io.github.deopping.bamboo.database.api.redis

import io.github.deopping.bamboo.database.api.Database
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands

/**
 * Represents a managed Redis connection.
 *
 * The database exposes a native Lettuce client, connection, and
 * synchronous command API using string keys and values. The client and
 * connection are owned by Bamboo and are closed when this database
 * is closed.
 *
 * The exposed connection is shared. Operations that change connection
 * state, such as MUTLI/WATCH, or blocking commands should use a separate
 * connection instead of sharing this connection concurrently.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface RedisDatabase : Database {

    /**
     * The Redis client owned by this database.
     * @since 0.1.0
     */
    val client: RedisClient

    /**
     * The shared Redis connection.
     * @since 0.1.0
     */
    val connection: StatefulRedisConnection<String, String>

    /**
     * Synchronous commands using the shared connection.
     * @since 0.1.0
     */
    val commands: RedisCommands<String, String>

}