package io.github.deopping.bamboo.database.internal

import io.github.deopping.bamboo.database.api.Database
import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema

/**
 * Creates and initializes database instances
 *
 * Implementations are responsible for establishing the connection,
 * applying the supplied schema when present, and cleaning up any
 * partially initialized resources if creation failed.
 *
 * @author DeOpping
 * @since 0.1.0
 */
internal fun interface DatabaseFactory {

    fun create(
        name: String,
        configuration: DatabaseConfiguration,
        schema: DatabaseSchema?
    ): Database

}