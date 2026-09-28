package io.github.deopping.bamboo.configuration.api.schema

/**
 * Validates values assigned to a configuration setting.
 *
 * This interface is a Kotlin SAM interface, allowing validators to be
 * declared using lambdas.
 *
 * @param T expected value type
 * @author DeOpping
 * @since 0.1.0
 */
fun interface ConfigValidator<T> {

    /**
     * Determines whether a value is valid.
     *
     * @param value value to validate
     * @return `true` when the value is valid, `false` otherwise
     * @since 0.1.0
     */
    fun validate(value: T): Boolean

}