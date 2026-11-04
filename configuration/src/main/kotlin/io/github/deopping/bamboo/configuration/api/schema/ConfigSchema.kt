package io.github.deopping.bamboo.configuration.api.schema

import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigration
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersioning
import java.util.Optional

/**
 * Defines the structure, details, and migrations of a configuration.
 *
 * A schema describes the settings supported by a configuration,
 * its versioning strategy, and the migrations used to update older versions.
 *
 * Schemas are used when loading configurations to initialize missing
 * settings, validate existing values, apply comments, and migrate
 * configurations to the target schema version.
 *
 * @property versioning the configuration versioning strategy, if configured
 * @property settings the settings defined by this schema, indexed by configuration path
 * @property migrations the migrations defined by this schema, indexed by source version
 * @property rootComment the root configuration comment, if configured
 * @property rootCommentAnchor the path used to anchor the root comment, if configured
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigSchema {
    /**
     * Gets the configuration versioning strategy.
     *
     * The versioning strategy determines how the current schema version
     * is stored and retrieved.
     *
     * @return the versioning strategy, or an empty optional if versioning is not
     * configured
     * @since 1.0.0
     */
    public val versioning: Optional<ConfigVersioning>

    /**
     * Gets the settings defined by this schema.
     *
     * Settings are indexed by their configuration paths.
     *
     * @return the settings defined by this schema
     * @since 1.0.0
     */
    public val settings: Map<String, ConfigSetting<*>>

    /**
     * Gets the migrations defined by this schema.
     *
     * Migrations are indexed by their source configuration versions.
     * Each migration describes how to advance a configuration from
     * one version to a later version.
     *
     * @return the migrations defined by this schema
     * @since 1.0.0
     */
    public val migrations: Map<Int, ConfigMigration>

    /**
     * Gets the root configuration comment.
     *
     * The comment is associated with [rootCommentAnchor] when applied
     * to a configuration that supports comments.
     *
     * @return the root comment, or an empty optional if none is configured
     * @since 1.0.0
     */
    public val rootComment: Optional<String>

    /**
     * Gets the configuration path used to anchor the root comment.
     *
     * This property is only present when a root comment has been configured.
     *
     * @return the root comment anchor path, or an empty optional if none is configured
     * @since 1.0.0
     */
    public val rootCommentAnchor: Optional<String>
}
