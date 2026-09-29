package io.github.deopping.bamboo.database.internal.configurations

import io.github.deopping.bamboo.database.api.DatabaseConfiguration
import io.github.deopping.bamboo.database.api.DatabaseFamily
import io.github.deopping.bamboo.database.api.DatabaseType

internal data class SqlDatabaseConfiguration(
    override val type: DatabaseType,
    val jdbcUrl: String,
    val username: String? = null,
    val password: String? = null,
    val properties: Map<String, Any?> = emptyMap()
) : DatabaseConfiguration {

    init {
        require(type.family == DatabaseFamily.SQL)
    }

}