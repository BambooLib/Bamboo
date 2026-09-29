package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseType

internal data class MongoDatabaseConfiguration(
    val connectionString: String,
    val database: String
) : DatabaseConfiguration {

    override val type: DatabaseType = DatabaseType.MONGODB

    init {
        require(type.family == DatabaseFamily.DOCUMENT)
    }

}