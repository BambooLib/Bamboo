package io.github.deopping.bamboo.configuration.internal.schema.migration

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigrationScope
import io.github.deopping.bamboo.configuration.internal.ConfigPath

internal class DefaultConfigMigrationScope(
    override val config: Config,
) : ConfigMigrationScope {
    override fun set(
        path: String,
        value: Any,
    ) {
        ConfigPath.validatePath(path)
        config.set(path, value)
    }

    override fun remove(path: String): Boolean {
        ConfigPath.validatePath(path)
        return config.remove(path)
    }

    override fun rename(
        from: String,
        to: String,
        overwrite: Boolean,
    ) {
        ConfigPath.validatePath(from)
        ConfigPath.validatePath(to)

        if (!config.contains(from)) {
            return
        }

        if (!overwrite && config.contains(to)) {
            throw ConfigException(
                "Cannot rename '$from' to '$to': destination already exists.",
            )
        }

        val value =
            config.get(from).orElseThrow {
                ConfigException("Cannot rename '$from': the source value is missing.")
            }

        val sourceComment = config.getComment(from)

        config.set(to, value)

        if (config.format.supportsComments &&
            sourceComment.isPresent &&
            !config.getComment(to).isPresent
        ) {
            config.setComment(to, sourceComment.get())
        }

        config.remove(from)
    }

    override fun copy(
        from: String,
        to: String,
        overwrite: Boolean,
    ) {
        ConfigPath.validatePath(from)
        ConfigPath.validatePath(to)

        if (!config.contains(from)) {
            return
        }

        if (!overwrite && config.contains(to)) {
            throw ConfigException(
                "Cannot copy '$from' to '$to': destination already exists.",
            )
        }

        val value =
            config.get(from).orElseThrow {
                ConfigException("Cannot copy '$from': the source value is missing.")
            }

        config.set(to, value)
    }
}
