package io.github.deopping.bamboo.configuration.internal.migration

import io.github.deopping.bamboo.configuration.api.migration.ConfigMigrationAction

internal interface ManagedConfigMigration {

    val action: ConfigMigrationAction

}