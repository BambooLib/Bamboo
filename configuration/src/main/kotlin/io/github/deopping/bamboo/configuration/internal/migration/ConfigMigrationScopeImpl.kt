package io.github.deopping.bamboo.configuration.internal.migration

import io.github.deopping.bamboo.configuration.api.Config
import io.github.deopping.bamboo.configuration.api.ConfigException
import io.github.deopping.bamboo.configuration.api.migration.ConfigMigrationScope

class ConfigMigrationScopeImpl internal constructor(
    override val config: Config
) : ConfigMigrationScope {

    override fun set(path: String, value: Any?) {
        config.set(path, value)
    }

    override fun remove(path: String): Boolean {
        return config.remove(path)
    }

    override fun rename(
        from: String,
        to: String,
        overwrite: Boolean
    ) {
        if (!config.contains(from)) {
            return
        }

        if (!overwrite && config.contains(to)) {
            throw ConfigException(
                "Cannot rename '$from' to '$to': destination already exists."
            )
        }

        config.set(to, config.get(from))

        if (config.format.supportsComments) {
            val comment = config.getComment(from)

            if (comment != null &&
                config.getComment(to) == null
            ) {
                config.setComment(to, comment)
            }
        }

        config.remove(from)
    }

    override fun copy(
        from: String,
        to: String,
        overwrite: Boolean
    ) {
        if (!config.contains(from)) {
            return
        }

        if (!overwrite && config.contains(to)) {
            throw ConfigException(
                "Cannot copy '$from' to '$to': destination already exists."
            )
        }

        config.set(to, config.get(from))
    }

}