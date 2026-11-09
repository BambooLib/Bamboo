package io.github.deopping.bamboo.common.api.namespace

import java.util.Optional

/**
 * Represents a namespaced resource identifier.
 *
 * A namespaced key consists of a namespace and a resource key,
 * separated by a colon.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public data class NamespacedKey(
    public val namespace: Namespace,
    public val key: String,
) {
    init {
        require(isValidKey(key)) {
            "Invalid resource key: '$key'."
        }
    }

    /**
     * Returns the fully qualified namespaced key.
     *
     * @return the namespaced key in namespace:key format
     * @since 1.0.0
     */
    public override fun toString(): String {
        return "${namespace.name}:$key"
    }

    public companion object {
        /**
         * Attempts to parse a fully qualified namespaced key.
         *
         * @param value the namespaced key
         * @return the parsed key, or an empty optional if invalid
         * @since 1.0.0
         */
        public fun parse(value: String): Optional<NamespacedKey> {
            val separator = value.indexOf(':')

            if (separator <= 0 || separator == value.lastIndex) {
                return Optional.empty()
            }

            val namespace = Namespace.parse(value.substring(0, separator))

            if (namespace.isEmpty) {
                return Optional.empty()
            }

            val key = value.substring(separator + 1)

            if (!isValidKey(key)) {
                return Optional.empty()
            }

            return Optional.of(NamespacedKey(namespace.get(), key))
        }

        /**
         * Creates a namespaced key from a namespace name and resource key.
         *
         * @param namespace the namespace name
         * @param key the resource key
         * @return the namespaced key
         * @throws IllegalArgumentException if the namespace or key is invalid
         * @since 1.0.0
         */
        public fun of(
            namespace: String,
            key: String,
        ): NamespacedKey {
            return NamespacedKey(
                Namespace(namespace),
                key,
            )
        }
    }
}

private fun isValidKey(key: String): Boolean {
    return key.isNotEmpty() &&
        key.all { character ->
            character in 'a'..'z' ||
                character in '0'..'9' ||
                character == '_' ||
                character == '-' ||
                character == '.' ||
                character == '/'
        }
}
