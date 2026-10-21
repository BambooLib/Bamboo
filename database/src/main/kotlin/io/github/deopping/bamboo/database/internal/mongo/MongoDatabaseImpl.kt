package io.github.deopping.bamboo.database.internal.mongo

import com.mongodb.client.MongoClient
import io.github.deopping.bamboo.database.api.DatabaseState
import io.github.deopping.bamboo.database.api.DatabaseType
import io.github.deopping.bamboo.database.api.mongo.MongoDatabase
import io.github.deopping.bamboo.database.internal.DatabaseFailures
import org.bson.Document
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class MongoDatabaseImpl(
    override val name: String,
    override val client: MongoClient,
    override val mongoDatabase: com.mongodb.client.MongoDatabase
) : MongoDatabase {

    override val type: DatabaseType = DatabaseType.MONGODB

    private val lifecycleLock = ReentrantReadWriteLock()

    @Volatile
    private var currentState = DatabaseState.CONNECTING

    override val state: DatabaseState
        get() = currentState

    internal fun initialize() {
        lifecycleLock.write {
            check(currentState == DatabaseState.CONNECTING) {
                "MongoDB database '$name' has already been initialized."
            }

            try {
                mongoDatabase.runCommand(Document("ping", 1))
                currentState = DatabaseState.CONNECTED
            } catch (failure: Exception) {
                currentState = DatabaseState.FAILED

                throw DatabaseFailures.create(
                    "Failed to initialize MongoDB database '$name'.",
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
            mongoDatabase.runCommand(Document("ping", 1))
            true
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

            try {
                client.close()
                currentState = DatabaseState.CLOSED
            } catch (failure: Exception) {
                currentState = DatabaseState.FAILED

                throw DatabaseFailures.create(
                    "Failed to close MongoDB database '$name'.",
                    failure
                )
            }
        }
    }

}