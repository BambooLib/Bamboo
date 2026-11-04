package io.github.deopping.bamboo.configuration.api.schema

/**
 * Validates values assigned to a configuration setting.
 *
 * A validator defines a validation rule for a particular value type.
 * It can be supplied to a [ConfigSettingBuilder] to determine whether
 * existing configuration values satisfy the setting's requirements.
 *
 * This is a functional interface and can be implemented using a lambda.
 *
 * @param T the type of value accepted by this validator
 *
 * @author DeOpping
 * @since 1.0.0
 */
public fun interface ConfigValidator<T> {
    /**
     * Checks whether the supplied value satisfies this validator's rules.
     *
     * @param value the value to validate
     * @return `true` if the value is valid, otherwise `false`
     * @since 1.0.0
     */
    public fun validate(value: T): Boolean
}
