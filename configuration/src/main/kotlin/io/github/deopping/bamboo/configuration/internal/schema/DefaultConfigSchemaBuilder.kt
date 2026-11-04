package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchemaBuilder
import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigSettingBuilder
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigrationAction
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersionStore
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersioning
import io.github.deopping.bamboo.configuration.internal.ConfigPath
import io.github.deopping.bamboo.configuration.internal.schema.migration.DefaultConfigMigration
import java.util.Optional

internal class DefaultConfigSchemaBuilder : ConfigSchemaBuilder {
    private val settings = linkedMapOf<String, ConfigSettingBuilder<*>>()
    private val migrations = linkedMapOf<Int, ConfigMigration>()

    private var versioning: ConfigVersioning? = null
    private var rootComment: String? = null
    private var rootCommentAnchor: String? = null

    override fun version(
        version: Int,
        path: String,
    ): ConfigSchemaBuilder {
        validateVersion(version)

        ConfigPath.validatePath(path)

        versioning =
            ConfigVersioning.Embedded(
                version = version,
                path = path,
            )

        return this
    }

    override fun externalVersion(
        version: Int,
        store: ConfigVersionStore,
    ): ConfigSchemaBuilder {
        validateVersion(version)

        versioning =
            ConfigVersioning.External(
                version = version,
                store = store,
            )

        return this
    }

    override fun <T : Any> setting(
        path: String,
        defaultValue: T,
    ): ConfigSettingBuilder<T> {
        ConfigPath.validatePath(path)

        require(path !in settings) {
            "Configuration setting already defined: $path"
        }

        val builder =
            DefaultConfigSettingBuilder(
                path = path,
                defaultValue = defaultValue,
            )

        settings[path] = builder

        return builder
    }

    override fun rootComment(
        anchorPath: String,
        comment: String,
    ): ConfigSchemaBuilder {
        ConfigPath.validatePath(anchorPath)

        require(comment.isNotBlank()) {
            "Root comment cannot be blank."
        }

        rootCommentAnchor = anchorPath
        rootComment = comment

        return this
    }

    override fun migration(
        from: Int,
        to: Int,
        action: ConfigMigrationAction,
    ): ConfigSchemaBuilder {
        require(from !in migrations) {
            "A migration from version '$from' has already been defined."
        }

        migrations[from] =
            DefaultConfigMigration(
                from = from,
                to = to,
                action = action,
            )

        return this
    }

    override fun build(): ConfigSchema {
        val builtSettings: Map<String, ConfigSetting<*>> =
            settings.mapValues { (_, builder) ->
                builder.build()
            }

        return DefaultConfigSchema(
            versioning = Optional.ofNullable(versioning),
            settings = builtSettings.toMap(),
            migrations = migrations.toMap(),
            rootComment = Optional.ofNullable(rootComment),
            rootCommentAnchor = Optional.ofNullable(rootCommentAnchor),
        )
    }

    private fun validateVersion(version: Int) {
        require(version >= 0) {
            "Configuration version cannot be negative."
        }
    }
}
