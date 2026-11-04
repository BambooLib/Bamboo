package io.github.deopping.bamboo.configuration.internal

internal object ConfigPath {
    internal fun parse(path: String): List<String> {
        if (path.isEmpty()) {
            return emptyList()
        }

        val parts = path.split('.')

        validateParts(path, parts)

        return parts
    }

    internal fun validatePath(path: String) {
        require(path.isNotBlank()) {
            "Configuration path cannot be blank."
        }

        val parts = path.split('.')

        validateParts(path, parts)
    }

    private fun validateParts(
        path: String,
        parts: List<String>,
    ) {
        require(parts.none { it.isEmpty() }) {
            "Configuration path contains an empty component: $path"
        }
    }
}
