package io.github.deopping.bamboo.configuration.api.schema

import io.github.deopping.bamboo.configuration.api.migration.ConfigMigration

/**
 * Describes the expected structure and evolution of a configuration.
 *
 * A schema can define default values, comments, validation rules, and
 * migrations between configuration versions.
 *
 * Schemas are optional. A configuration does not need a schema to be loaded,
 * read, modified, or saved.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigSchema {

    /**
     * The versioning strategy used by this schema, or `null` when
     * versioning is disabled.
     * @since 0.1.0
     */
    val versioning: ConfigVersioning?

    /**
     * The settings defined by this schema, keyed by their configuration path.
     * @since 0.1.0
     */
    val settings: Map<String, ConfigSetting>

    /**
     * The migrations defined by this schema, keyed by their source version.
     * @since 0.1.0
     */
    val migrations: Map<Int, ConfigMigration>

    /**
     * The configuration path of the setting used to anchor the root comment,
     * or `null` if no root comment is defined.
     *
     * Because the underlying configuration format may not support file-level
     * comments, the root comment is associated with this setting instead.
     *
     * @since 0.1.0
     */
    val rootCommentAnchor: String?

    /**
     * The comment intended to appear at the top of the configuration file,
     * or `null` if no root comment is defined.
     *
     * When the configuration format does not support file-level comments,
     * this comment is associated with the setting identified by
     * [rootCommentAnchor].
     *
     * @since 0.1.0
     */
    val rootComment: String?

}