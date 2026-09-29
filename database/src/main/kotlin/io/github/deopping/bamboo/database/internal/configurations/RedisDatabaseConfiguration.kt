package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseType

internal data class RedisDatabaseConfiguration(
    val connectionString: String
) : DatabaseConfiguration {

    override val type: DatabaseType = DatabaseType.REDIS

    init {
        require(type.family == DatabaseFamily.KEY_VALUE)
    }

}