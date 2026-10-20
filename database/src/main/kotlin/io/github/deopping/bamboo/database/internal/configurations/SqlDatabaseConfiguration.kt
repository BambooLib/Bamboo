package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseType

internal data class SqlDatabaseConfiguration(
    override val type: DatabaseType,
    override val jdbcUrl: String,
    override val username: String? = null,
    override val password: String? = null,
    override val properties: Map<String, Any?> = emptyMap()
) : DatabaseConfiguration.Sql {

    init {
        require(type.family == DatabaseFamily.SQL)
    }

    override fun toString(): String =
        "SqlDatabaseConfiguration(type=$type, connectionDetails=<redacted>)"

}