package io.github.deopping.bamboo.configuration.internal.schema.migration

import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigrationAction

internal class DefaultConfigMigration(
    override val from: Int,
    override val to: Int,
    override val action: ConfigMigrationAction,
) : ConfigMigration {
    init {
        validateFrom(from)
        validatePath(from, to)
    }

    internal companion object {
        internal fun validateFrom(from: Int) {
            require(from >= 0) {
                "Migration source version '$from' must not be negative."
            }
        }

        internal fun validatePath(
            from: Int,
            to: Int,
        ) {
            require(to > from) {
                "Migration target version '$to' must be greater than its source '$from'."
            }
        }
    }
}
