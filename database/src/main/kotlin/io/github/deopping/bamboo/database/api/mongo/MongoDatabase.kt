package io.github.deopping.bamboo.database.api.mongo

import com.mongodb.client.MongoClient
import io.github.deopping.bamboo.database.api.Database

/**
 * Represents a managed MongoDB database.
 *
 * The native driver API is exposed for collection and document operations.
 * The client is owned by Bamboo and is closed when this database is closed.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface MongoDatabase : Database {

    /**
     * The MongoDB client owned by this database.
     * @since 0.1.0
     */
    val client: MongoClient

    /**
     * The selected native MongoDB database.
     * @since 0.1.0
     */
    val mongoDatabase: com.mongodb.client.MongoDatabase

}
