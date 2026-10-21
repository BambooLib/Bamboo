package io.github.deopping.bamboo.database.internal.migration

import io.github.deopping.bamboo.database.api.migration.DatabaseMigration
import io.github.deopping.bamboo.database.api.migration.DatabaseMigrationAction

internal class DatabaseMigrationImpl(
    override val from: Int,
    override val to: Int,
    override val action: DatabaseMigrationAction
) : DatabaseMigration, ManagedDatabaseMigration