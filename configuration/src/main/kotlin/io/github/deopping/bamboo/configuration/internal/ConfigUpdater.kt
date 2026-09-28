package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigException
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigVersioning
import io.github.deopping.bamboo.configuration.internal.migration.ConfigMigrationImpl
import io.github.deopping.bamboo.configuration.internal.migration.ConfigMigrationScopeImpl
import io.github.deopping.bamboo.configuration.internal.migration.ManagedConfigMigration

internal object ConfigUpdater {

    fun update(
        config: Config,
        schema: ConfigSchema,
        newlyCreated: Boolean
    ) {
        val versioning = schema.versioning

        if (versioning != null) {
            updateVersioned(
                config = config,
                schema = schema,
                versioning = versioning,
                newlyCreated = newlyCreated
            )
        }

        applySettings(
            config = config,
            schema = schema
        )

        if (versioning is ConfigVersioning.Embedded) {
            config.set(
                versioning.path,
                versioning.version
            )
        }

        if (config.isDirty) {
            config.save()
        }

        if (versioning is ConfigVersioning.External) {
            versioning.store.setVersion(
                config.path,
                versioning.version
            )
        }
    }

    private fun updateVersioned(
        config: Config,
        schema: ConfigSchema,
        versioning: ConfigVersioning,
        newlyCreated: Boolean
    ) {
        val targetVersion = versioning.version

        var currentVersion = readVersion(
            config = config,
            versioning = versioning,
            newlyCreated = newlyCreated
        )

        if (currentVersion > targetVersion) {
            throw ConfigException(
                "Configuration '${config.path}' uses version $$currentVersion, " +
                "but the current schema version is $targetVersion."
            )
        }

        while (currentVersion < targetVersion) {
            val migration = schema.migrations[currentVersion]
                ?: throw ConfigException(
                    "No migration exists from configuration version " +
                    "$currentVersion to $targetVersion."
                )

            if (migration.from != currentVersion) {
                throw ConfigException(
                    "Invalid migration source: " +
                    "${migration.from} -> ${migration.to}. " +
                    "Expected source version $currentVersion."
                )
            }

            if (migration.to <= currentVersion) {
                throw ConfigException(
                    "Invalid migration: " +
                    "${migration.from} -> ${migration.to}. " +
                    "Migration target must be greater than its source."
                )
            }

            if (migration.to > targetVersion) {
                throw ConfigException(
                    "Migration ${migration.from} -> ${migration.to} " +
                    "exceeds the current schema version $targetVersion."
                )
            }

            if (migration is ManagedConfigMigration) {
                migration.action.apply(
                    scope = ConfigMigrationScopeImpl(config)
                )
            }

            currentVersion = migration.to
        }
    }

    private fun applySettings(
        config: Config,
        schema: ConfigSchema
    ) {
        schema.settings.values.forEach { setting ->
            if (!config.contains(setting.path)) {
                config.set(
                    setting.path,
                    setting.defaultValue
                )
            } else {
                val value = config.get(setting.path)

                if (!setting.isValid(value)) {
                    config.set(
                        setting.path,
                        setting.defaultValue
                    )
                }
            }

            if (setting.comment != null &&
                config.format.supportsComments &&
                config.getComment(setting.path) == null
            ) {
                config.setComment(
                    setting.path,
                    setting.comment
                )
            }
        }
    }

    private fun readVersion(
        config: Config,
        versioning: ConfigVersioning,
        newlyCreated: Boolean
    ): Int {
        val storedVersion = when (versioning) {
            is ConfigVersioning.Embedded ->
                config.getInt(versioning.path)

            is ConfigVersioning.External ->
                versioning.store.getVersion(config.path)
        }

        if (storedVersion != null) {
            return storedVersion
        }

        if (newlyCreated) {
            return versioning.version
        }

        throw ConfigException(
            "Configuration '${config.path}' has no stored schema version."
        )
    }

}