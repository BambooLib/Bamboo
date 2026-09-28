package io.github.deopping.bamboo.configuration.api.schema

/**
 * Builds a configuration setting for use in a [ConfigSchema].
 *
 * Instances are created by [ConfigSchemaBuilder] and should not normally
 * be constructed directly.
 *
 * @param T setting value type
 * @author DeOpping
 * @since 0.1.0
 */
interface SettingBuilder<T> {

    /**
     * The configuration path being defined.
     * @since 0.1.0
     */
    val path: String

    /**
     * The default value for the setting.
     * @since 0.1.0
     */
    val defaultValue: T

    /**
     * The default comment associated with the setting.
     * @since 0.1.0
     */
    var comment: String?

    /**
     * Adds a validation rule to this setting.
     *
     * @param validator validator applied to the setting value
     * @return this builder
     * @since 0.1.0
     */
    fun validate(validator: ConfigValidator<T>): SettingBuilder<T>

}