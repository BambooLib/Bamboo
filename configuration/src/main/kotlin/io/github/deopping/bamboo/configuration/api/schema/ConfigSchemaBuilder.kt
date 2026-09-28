package io.github.deopping.bamboo.configuration.api.schema

import io.github.deopping.bamboo.configuration.ConfigApi
import io.github.deopping.bamboo.configuration.api.migration.ConfigMigrationAction
import io.github.deopping.bamboo.configuration.api.migration.ConfigMigrationScope

/**
 * Builds a [ConfigSchema].
 *
 * Implementations are supplied by Bamboo and should normally be obtained
 * through [ConfigSchema].
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigSchemaBuilder {

    /**
     * Enables embedded configuration versioning.
     *
     * The version is stored directly inside the configuration.
     *
     * @param version current schema version
     * @param path configuration path used to store the version
     * @since 0.1.0
     */
    fun version(
        version: Int,
        path: String = "config-version"
    )

    /**
     * Enables externally stored configuration versioning.
     *
     * @param version current schema version
     * @param store external version store
     * @since 0.1.0
     */
    fun externalVersion(
        version: Int,
        store: ConfigVersionStore
    )

    /**
     * Defines a configuration setting.
     *
     * The returned builder can be further configured using a comment
     * and validation rules.
     *
     * @param path configuration path
     * @param defaultValue default value
     * @return a builder for the new setting
     * @since 0.1.0
     */
    fun <T> setting(
        path: String,
        defaultValue: T
    ): SettingBuilder<T>

    /**
     * Adds a migration to the schema.
     *
     * This method is the Java-friendly form of the Kotlin [migrate] DSL.
     *
     * @param from source schema version
     * @param to target schema version
     * @param action migration action
     * @since 0.1.0
     */
    fun migration(
        from: Int,
        to: Int,
        action: ConfigMigrationAction
    )

    /**
     * Builds the final immutable schema.
     *
     * @return constructed configuration schema
     * @throws IllegalStateException if the schema is invalid
     * @since 0.1.0
     */
    fun build(): ConfigSchema

}

/**
 * Configures a setting using a Kotlin DSL.
 *
 * @param path configuration path
 * @param defaultValue default value
 * @param configure setting configuration
 * @return the configured setting builder
 * @since 0.1.0
 */
fun <T> ConfigSchemaBuilder.setting(
    path: String,
    defaultValue: T,
    configure: SettingBuilder<T>.() -> Unit
): SettingBuilder<T> {
    return setting(path, defaultValue).apply(configure)
}

/**
 * Adds a migration using a Kotlin DSL.
 *
 * @param from source schema version
 * @param to target schema version
 * @param configure migration configuration
 * @since 0.1.0
 */
fun ConfigSchemaBuilder.migrate(
    from: Int,
    to: Int,
    configure: ConfigMigrationScope.() -> Unit
) {
    migration(from, to) { scope -> configure(scope) }
}

/**
 * Creates a configuration schema using Kotlin's DSL syntax.
 *
 * @param configure schema builder configuration
 * @return the constructed [ConfigSchema]
 * @since 0.1.0
 */
fun ConfigApi.configSchema(
    configure: ConfigSchemaBuilder.() -> Unit
): ConfigSchema {
    return createConfigSchemaBuilder()
        .apply(configure)
        .build()
}