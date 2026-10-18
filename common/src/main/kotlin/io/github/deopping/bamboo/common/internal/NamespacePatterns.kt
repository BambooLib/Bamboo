package io.github.deopping.bamboo.common.internal

/**
 * Contains the validation patterns used by the common module.
 *
 * @author DeOpping
 * @since 0.1.0
 */
internal object NamespacePatterns {

    /**
     * Validates namespace components.
     *
     * Allowed characters: lowercase letters, digits, periods,
     * underscores, and hyphens.
     */
    val NAMESPACE = Regex("[a-z0-9._-]+")

    /**
     * Validates key components.
     *
     * Allowed characters: lowercase letters, digits, periods,
     * underscores, hyphens, and forward slashes.
     */
    val KEY = Regex("[a-z0-9/._-]+")

}