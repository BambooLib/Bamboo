package io.github.deopping.bamboo.database.internal.migration

import io.github.deopping.bamboo.database.api.migration.DatabaseMigrationAction

internal interface ManagedDatabaseMigration {
    val action: DatabaseMigrationAction
}