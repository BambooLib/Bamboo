package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseType

internal data class MongoDatabaseConfiguration(
    override val connectionString: String,
    override val database: String
) : DatabaseConfiguration.Mongo {

    override val type: DatabaseType = DatabaseType.MONGODB

    init {
        require(type.family == DatabaseFamily.DOCUMENT)
    }

    override fun toString(): String =
        "MongoDatabaseConfiguration(type=$type, connectionDetails=<redacted>)"

}