package io.github.deopping.bamboo.configuration.internal

internal object ConfigPath {

    fun parse(path: String): List<String> {
        if (path.isEmpty()) {
            return emptyList()
        }

        val parts = path.split('.')

        require(parts.none { it.isEmpty() }) {
            "Configuration path contains an empty component: $path"
        }

        return parts
    }

}