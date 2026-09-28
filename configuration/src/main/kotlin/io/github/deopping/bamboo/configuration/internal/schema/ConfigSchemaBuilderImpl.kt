package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.migration.ConfigMigrationAction
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchemaBuilder
import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigVersionStore
import io.github.deopping.bamboo.configuration.api.schema.ConfigVersioning
import io.github.deopping.bamboo.configuration.api.schema.SettingBuilder
import io.github.deopping.bamboo.configuration.internal.migration.ConfigMigrationImpl

internal class ConfigSchemaBuilderImpl : ConfigSchemaBuilder {

    private val settings = linkedMapOf<String, SettingBuilderImpl<*>>()
    private val migrations = linkedMapOf<Int, ConfigMigrationImpl>()

    private var versioning: ConfigVersioning? = null

    override fun version(version: Int, path: String) {
        require(version >= 0) {
            "Configuration version path cannot be negative."
        }

        require(path.isNotBlank()) {
            "Configuration version path cannot be empty."
        }

        versioning = ConfigVersioning.Embedded(
            version = version,
            path = path
        )
    }

    override fun externalVersion(
        version: Int,
        store: ConfigVersionStore
    ) {
        require(version >= 0) {
            "Configuration version cannot be negative."
        }

        versioning = ConfigVersioning.External(
            version = version,
            store = store
        )
    }

    override fun <T> setting(
        path: String,
        defaultValue: T
    ): SettingBuilder<T> {
        require(path.isNotBlank()) {
            "Configuration setting path cannot be empty."
        }

        require(path !in settings) {
            "Configuration setting already defined: $path"
        }

        val builder = SettingBuilderImpl(
            path = path,
            defaultValue = defaultValue
        )

        settings[path] = builder

        return builder
    }

    override fun migration(
        from: Int,
        to: Int,
        action: ConfigMigrationAction
    ) {
        require(from >= 0) {
            "Migration source version cannot be negative."
        }

        require(to > from) {
            "Migration target version must be greater than source version."
        }

        require(from !in migrations) {
            "A migration from version $from has already been defined."
        }

        migrations[from] = ConfigMigrationImpl(
            from = from,
            to = to,
            action = action
        )
    }

    override fun build(): ConfigSchema {
        if (migrations.isNotEmpty() && versioning == null) {
            throw IllegalStateException(
                "Migrations require configuration versioning."
            )
        }

        versioning?.let { versioning ->
            migrations.values.forEach { migration ->
                require(migration.to <= versioning.version) {
                    "Migration ${migration.from} -> ${migration.to} " +
                    "exceeds schema version ${versioning.version}."
                }
            }
        }

        val builtSettings: Map<String, ConfigSetting> =
            settings.mapValues { (_, builder) ->
                builder.build()
            }

        val builtMigrations: Map<Int, ConfigMigration> =
            migrations.toMap()

        return ConfigSchemaImpl(
            versioning = versioning,
            settings = builtSettings,
            migrations = builtMigrations
        )
    }

}