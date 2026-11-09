package io.github.deopping.bamboo.common.api.namespace

import java.util.Optional

/**
 * Represents a namespace used to identify a resource.
 *
 * A namespace may contain lowercase ASCII letters, digits, underscores,
 * hyphens, and periods.
 *
 * @author DeOpping
 * @since 1.0.0
 */
@JvmInline
public value class Namespace(
    public val name: String,
) {
    init {
        require(isValidName(name)) {
            "Invalid namespace: '$name'."
        }
    }

    /**
     * Returns the namespace's string representation.
     *
     * @return the namespace name
     * @since 1.0.0
     */
    public override fun toString(): String = name

    public companion object {
        /**
         * Attempts to parse a namespace.
         *
         * @param name the namespace name
         * @return the parsed namespace, or an empty optional if invalid
         * @since 1.0.0
         */
        public fun parse(name: String): Optional<Namespace> {
            return if (isValidName(name)) {
                Optional.of(Namespace(name))
            } else {
                Optional.empty()
            }
        }
    }
}

private fun isValidName(name: String): Boolean {
    return name.isNotEmpty() &&
        name.all { character ->
            character in 'a'..'z' ||
                character in '0'..'9' ||
                character == '_' ||
                character == '-' ||
                character == '.'
        }
}
