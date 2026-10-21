package io.github.deopping.bamboo.database.internal.schema

import io.github.deopping.bamboo.database.api.migration.DatabaseMigration
import io.github.deopping.bamboo.database.api.schema.DatabaseSchema
import java.util.Collections

internal class DatabaseSchemaImpl(
    override val version: Int,
    migrations: Map<Int, DatabaseMigration>
) : DatabaseSchema {

    override val migrations: Map<Int, DatabaseMigration> =
        Collections.unmodifiableMap(LinkedHashMap(migrations))

}