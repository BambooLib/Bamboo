package io.github.deopping.bamboo.configuration.api.schema

/**
 * Defines a single configuration setting.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigSetting {

    /**
     * The configuration path represented by this setting.
     * @since 0.1.0
     */
    val path: String

    /**
     * The default value used when the setting is missing or invalid.
     * @since 0.1.0
     */
    val defaultValue: Any?

    /**
     * The default comment associated with this setting, or `null` when no
     * comment is defined.
     * @since 0.1.0
     */
    val comment: String?

    /**
     * Determines whether a value satisfies this setting's validation rule.
     * @param value value to validate
     * @return `true` when the value is valid, `false` otherwise
     * @since 0.1.0
     */
    fun isValid(value: Any?): Boolean

}