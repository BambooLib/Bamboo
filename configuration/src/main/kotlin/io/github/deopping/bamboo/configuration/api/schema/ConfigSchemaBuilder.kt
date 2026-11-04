package io.github.deopping.bamboo.configuration.api.schema

import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigMigrationAction
import io.github.deopping.bamboo.configuration.api.schema.migration.ConfigVersionStore

/**
 * Builds a configuration schema.
 *
 * A schema builder defines the settings, versioning strategy, migrations,
 * and root comment associated with a configuration schema.
 *
 * Settings and migrations are accumulated by the builder and included
 * when [build] is called.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigSchemaBuilder {
    /**
     * Configures embedded configuration versioning.
     *
     * The version is stored directly in the configuration at the specified
     * path. The path identifies the configuration value used to track the
     * schema version.
     *
     * @param version the target configuration schema version
     * @param path the configuration path used to store the version
     * @return this builder
     * @throws IllegalArgumentException if the version is negative or the path is invalid
     * @since 1.0.0
     */
    public fun version(
        version: Int,
        path: String,
    ): ConfigSchemaBuilder

    /**
     * Configures externally stored configuration versioning.
     *
     * The version is managed by the supplied [ConfigVersionStore] rather
     * than being stored inside configuration file.
     *
     * @param version the target configuration schema version
     * @param store the store used to retrieve and update configuration versions
     * @return this builder
     * @throws IllegalArgumentException if the version is negative
     * @since 1.0.0
     */
    public fun externalVersion(
        version: Int,
        store: ConfigVersionStore,
    ): ConfigSchemaBuilder

    /**
     * Adds a setting to this schema.
     *
     * The returned builder can be used to configure the setting's comment
     * and validation rules. The completed setting is included in the schema
     * when [build] is called.
     *
     * Each setting must have a unique configuration path within the schema.
     *
     * @param T the type of the setting's default value
     * @param path the configuration path associated with the setting
     * @param defaultValue the default value assigned when the setting is missing or invalid
     * @return a builder for configuring the new setting
     * @throws IllegalArgumentException if the path is invalid or already defined
     * @since 1.0.0
     */
    public fun <T : Any> setting(
        path: String,
        defaultValue: T,
    ): ConfigSettingBuilder<T>

    /**
     * Configures the root comment for this schema.
     *
     * The comment is associated with the specified configuration path.
     * It is applied when the configuration format supports comments.
     *
     * @param anchorPath the configuration path used to anchor the comment
     * @param comment the root configuration comment
     * @return this builder
     * @throws IllegalArgumentException if the anchor path is invalid or the comment is blank
     * @since 1.0.0
     */
    public fun rootComment(
        anchorPath: String,
        comment: String,
    ): ConfigSchemaBuilder

    /**
     * Adds a migration between two configuration versions.
     *
     * Migrations are indexed by their source version and are used to
     * transform configurations when updating them to a newer schema version.
     *
     * The source version must be non-negative, the target version must be
     * greater than the source version, and each source version can only have
     * one migration.
     *
     * @param from the source configuration version
     * @param to the target configuration version
     * @param action the operation performed to migrate the configuration
     * @return this builder
     * @throws IllegalArgumentException if the versions are invalid or a migration
     * from the source version is already defined
     * @since 1.0.0
     */
    public fun migration(
        from: Int,
        to: Int,
        action: ConfigMigrationAction,
    ): ConfigSchemaBuilder

    /**
     * Builds the configuration schema.
     *
     * The resulting schema contains the settings, migrations, versioning
     * strategy, and root comment configured through this builder.
     *
     * A schema containing migrations must have a versioning strategy.
     * Every migration target version must also be within the configured
     * schema version.
     *
     * @return the constructed configuration schema
     * @throws IllegalArgumentException if the configured schema is invalid
     * @since 1.0.0
     */
    public fun build(): ConfigSchema
}
