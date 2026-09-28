package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigVersioning

internal class ConfigSchemaImpl(
    override val versioning: ConfigVersioning?,
    override val settings: Map<String, ConfigSetting>,
    override val migrations: Map<Int, ConfigMigration>
) : ConfigSchema