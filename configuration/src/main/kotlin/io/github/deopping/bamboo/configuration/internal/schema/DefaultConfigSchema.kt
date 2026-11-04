package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersioning
import io.github.deopping.bamboo.configuration.internal.ConfigPath
import java.util.Optional

internal class DefaultConfigSchema(
    override val versioning: Optional<ConfigVersioning>,
    override val settings: Map<String, ConfigSetting<*>>,
    override val migrations: Map<Int, ConfigMigration>,
    override val rootComment: Optional<String>,
    override val rootCommentAnchor: Optional<String>,
) : ConfigSchema {
    init {
        require(rootComment.isEmpty || rootCommentAnchor.isPresent) {
            "A root comment requires an anchor path"
        }

        if (rootComment.isPresent) {
            require(rootComment.get().isNotBlank()) {
                "Root comment cannot be blank."
            }
        }

        if (rootCommentAnchor.isPresent) {
            ConfigPath.validatePath(rootCommentAnchor.get())
        }

        require(migrations.isEmpty() || versioning.isPresent) {
            "Migrations require configuration versioning."
        }

        versioning.ifPresent { configVersioning ->
            migrations.values.forEach { migration ->
                require(migration.to <= configVersioning.version) {
                    "Migration ${migration.from} -> ${migration.to} " +
                        "exceeds schema version ${configVersioning.version}"
                }
            }
        }
    }
}
