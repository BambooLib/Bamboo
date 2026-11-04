package io.github.deopping.bamboo.configuration.api.schema

/**
 * Builds a configuration setting.
 *
 * A setting builder configures the optional comment and validation rules
 * associated with a configuration setting before constructing it.
 *
 * @param T the type of the setting's default value
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigSettingBuilder<T : Any> {
    /**
     * Sets the comment associated with this setting.
     *
     * The comment is intended to describe the setting in the configuration
     * file. It is applied when the configuration format supports comments.
     *
     * @param comment the setting comment
     * @return this builder
     * @throws IllegalArgumentException if the comment is blank
     * @since 1.0.0
     */
    public fun comment(comment: String): ConfigSettingBuilder<T>

    /**
     * Sets the validator used to validate this setting's values.
     *
     * The validator is evaluated when an existing configuration value is
     * checked against this setting.
     *
     * @param validator the validator used to check setting values
     * @return this builder
     * @since 1.0.0
     */
    public fun validator(validator: ConfigValidator<T>): ConfigSettingBuilder<T>

    /**
     * Builds the configuration setting.
     *
     * The returned setting contains the path and default value supplied
     * when this builder was created, along with its configured comment
     * and validator.
     *
     * @return the constructed configuration setting
     * @since 1.0.0
     */
    public fun build(): ConfigSetting<T>
}
