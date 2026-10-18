package io.github.deopping.bamboo.common.api

/**
 * Represents a validated namespace identifier.
 *
 * @property namespace the namespace owning this identifier
 * @property key the identifier's key component
 * @author DeOpping
 * @since 0.1.0
 */
data class NamespacedKey(
    val namespace: String,
    val key: String
) {

    init {
        NamespaceUtils.requireValidNamespace(namespace)
        NamespaceUtils.requireValidKey(key)
    }

    /**
     * Returns this identifier in namespaced form.
     *
     * @return the identifier in the format "namespace:key"
     * @since 0.1.0
     */
    override fun toString(): String = "$namespace:$key"

    companion object {
        /**
         * Parses a namespaced identifier.
         *
         * @param value the identifier to parse
         * @return the parsed identifier
         * @throws IllegalArgumentException if the identifier is invalid
         * @since 0.1.0
         */
        @JvmStatic
        fun parse(value: String): NamespacedKey {
            val separator = value.indexOf(':')

            require(
                separator > 0 &&
                separator == value.lastIndexOf(':') &&
                separator < value.lastIndex
            ) {
                "Invalid namespaced identifier: '$value'."
            }

            return NamespacedKey(
                namespace = value.substring(0, separator),
                key = value.substring(separator + 1)
            )
        }
    }

}
