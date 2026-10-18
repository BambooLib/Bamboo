package io.github.deopping.bamboo.common.api

import io.github.deopping.bamboo.common.internal.NamespacePatterns

/**
 * Provides utilities for validating namespaces and namespaced keys.
 *
 * @author DeOpping
 * @since 0.1.0
 */
object NamespaceUtils {

    /**
     * Checks whether a namespace is valid.
     *
     * @param namespace the namespace to validate
     * @return `true` if the namespace is valid, `false` otherwise
     * @since 0.1.0
     */
    @JvmStatic
    fun isValidNamespace(namespace: String): Boolean =
        NamespacePatterns.NAMESPACE.matches(namespace)

    /**
     * Checks whether a key component is valid.
     *
     * @param key the key to validate
     * @return `true` if the key is valid, `false` otherwise
     * @since 0.1.0
     */
    @JvmStatic
    fun isValidKey(key: String): Boolean =
        NamespacePatterns.KEY.matches(key)

    /**
     * Validates a namespace and returns it unchanged.
     *
     * @param namespace the namespace to validate
     * @return the validated namespace
     * @throws IllegalArgumentException if the namespace is invalid
     * @since 0.1.0
     */
    @JvmStatic
    fun requireValidNamespace(namespace: String): String {
        require(isValidNamespace(namespace)) {
            "Invalid namespace: '$namespace'."
        }

        return namespace
    }

    /**
     * Validates a key component and returns it unchanged.
     *
     * @param key the key to validate
     * @return the validated key
     * @throws IllegalArgumentException if the key is invalid
     * @since 0.1.0
     */
    @JvmStatic
    fun requireValidKey(key: String): String {
        require(isValidKey(key)) {
            "Invalid key: '$key'."
        }

        return key
    }

}