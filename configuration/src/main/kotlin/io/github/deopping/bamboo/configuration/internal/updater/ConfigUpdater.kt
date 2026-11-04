package io.github.deopping.bamboo.configuration.internal.updater

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersioning
import io.github.deopping.bamboo.configuration.internal.schema.migration.DefaultConfigMigrationScope

internal object ConfigUpdater {
    internal fun update(
        config: Config,
        schema: ConfigSchema,
        newlyCreated: Boolean,
    ) {
        try {
            val versioning = schema.versioning.orElse(null)

            if (versioning != null) {
                updateVersioned(
                    config = config,
                    schema = schema,
                    versioning = versioning,
                    newlyCreated = newlyCreated,
                )
            }

            applySettings(
                config = config,
                schema = schema,
            )

            applyRootComment(
                config = config,
                schema = schema,
            )

            when (versioning) {
                is ConfigVersioning.Embedded -> {
                    config.set(
                        versioning.path,
                        versioning.version,
                    )
                }

                is ConfigVersioning.External -> {
                    versioning.store.setVersion(
                        config.path,
                        versioning.version,
                    )
                }

                null -> Unit
            }

            if (config.isDirty) {
                config.save()
            }
        } catch (exception: ConfigException) {
            throw exception
        } catch (exception: Exception) {
            throw ConfigException(
                "Failed to update configuration schema for '${config.path}'.",
                exception,
            )
        }
    }

    private fun updateVersioned(
        config: Config,
        schema: ConfigSchema,
        versioning: ConfigVersioning,
        newlyCreated: Boolean,
    ) {
        val targetVersion = versioning.version

        var currentVersion =
            readVersion(
                config = config,
                versioning = versioning,
                newlyCreated = newlyCreated,
            )

        if (currentVersion > targetVersion) {
            throw ConfigException(
                "Configuration '${config.path}' uses version $$currentVersion, " +
                    "but the current schema version is $targetVersion.",
            )
        }

        while (currentVersion < targetVersion) {
            val migration =
                schema.migrations[currentVersion]
                    ?: throw ConfigException(
                        "No migration exists from configuration version " +
                            "$currentVersion to $targetVersion.",
                    )

            if (migration.from != currentVersion) {
                throw ConfigException(
                    "Invalid migration source: ${migration.from} -> ${migration.to}. " +
                        "Expected source version $currentVersion.",
                )
            }

            if (migration.to <= currentVersion) {
                throw ConfigException(
                    "Invalid migration ${migration.from} -> ${migration.to}: " +
                        "the target must be greater than the source.",
                )
            }

            if (migration.to > targetVersion) {
                throw ConfigException(
                    "Migration ${migration.from} -> ${migration.to} " +
                        "exceeds version $targetVersion.",
                )
            }

            migration.action.apply(
                DefaultConfigMigrationScope(config),
            )

            currentVersion = migration.to
        }
    }

    private fun readVersion(
        config: Config,
        versioning: ConfigVersioning,
        newlyCreated: Boolean,
    ): Int {
        val storedVersion =
            when (versioning) {
                is ConfigVersioning.Embedded ->
                    config.getInt(versioning.path)

                is ConfigVersioning.External ->
                    versioning.store.getVersion(config.path)
            }

        if (storedVersion.isPresent) {
            return storedVersion.get()
        }

        if (newlyCreated) {
            return versioning.version
        }

        throw ConfigException(
            "Configuration '${config.path}' has no stored schema version.",
        )
    }

    private fun applySettings(
        config: Config,
        schema: ConfigSchema,
    ) {
        for (setting in schema.settings.values) {
            if (!config.contains(setting.path)) {
                config.set(
                    setting.path,
                    setting.defaultValue,
                )
            } else {
                val value =
                    config.get(setting.path).orElseThrow {
                        ConfigException("Configuration value '${setting.path}' is missing.")
                    }

                if (!setting.isValid(value)) {
                    config.set(
                        setting.path,
                        setting.defaultValue,
                    )
                }
            }

            setting.comment.ifPresent { comment ->
                if (config.format.supportsComments &&
                    !config.getComment(setting.path).isPresent
                ) {
                    config.setComment(setting.path, comment)
                }
            }
        }
    }

    private fun applyRootComment(
        config: Config,
        schema: ConfigSchema,
    ) {
        if (!config.format.supportsComments) {
            return
        }

        if (schema.rootComment.isEmpty) {
            return
        }

        val comment = schema.rootComment.get()
        val anchor =
            schema.rootCommentAnchor.orElseThrow {
                ConfigException("A root comment was configured without a comment anchor.")
            }

        config.setComment(anchor, comment)
    }
}
