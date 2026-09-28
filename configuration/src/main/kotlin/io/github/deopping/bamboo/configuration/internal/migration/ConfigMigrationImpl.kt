package io.github.deopping.bamboo.configuration.internal.migration

import io.github.deopping.bamboo.configuration.api.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.migration.ConfigMigrationAction

internal class ConfigMigrationImpl(
    override val from: Int,
    override val to: Int,
    internal val action: ConfigMigrationAction
) : ConfigMigration