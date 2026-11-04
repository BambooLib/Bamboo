package io.github.deopping.bamboo.configuration.api.schema

import java.util.Optional

/**
 * Defines a setting supported by a configuration schema.
 *
 * A setting describes its configuration value, including its path, default
 * value, optional comment, and optional validation rules.
 *
 * Settings are used when applying a [ConfigSchema] to a configuration.
 * Missing values are initialized using their default values, while
 * existing values can be checked against their validation rules.
 *
 * @param T the type of the setting's default value
 *
 * @property path the configuration path of this setting
 * @property defaultValue the default value of this setting
 * @property comment the optional comment associated with this setting
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigSetting<T : Any> {
    /**
     * Gets the configuration path associated with this setting.
     *
     * @return the setting's configuration path
     * @since 1.0.0
     */
    public val path: String

    /**
     * Gets the default value associated with this setting.
     *
     * This value is used when the corresponding configuration value is
     * missing or does not satisfy the setting's validation rules.
     *
     * @return the setting's default value
     * @since 1.0.0
     */
    public val defaultValue: T

    /**
     * Gets the comment associated with this setting.
     *
     * @return the setting comment, or an empty optional if no comment is configured
     * @since 1.0.0
     */
    public val comment: Optional<String>

    /**
     * Checks whether the supplied value is valid for this setting.
     *
     * When a validator has been configured, the value is checked against
     * that validator. When no validator is configured, the value is
     * considered valid.
     *
     * @param value the value to validate
     * @return `true` if the value is valid, otherwise `false`
     * @since 1.0.0
     */
    public fun isValid(value: Any?): Boolean
}
