package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseType

internal data class RedisDatabaseConfiguration(
    override val connectionString: String
) : DatabaseConfiguration.Redis {

    override val type: DatabaseType = DatabaseType.REDIS

    init {
        require(type.family == DatabaseFamily.KEY_VALUE)
    }

    override fun toString(): String =
        "RedisDatabaseConfiguration(type=$type, connectionDetails=<redacted>)"

}