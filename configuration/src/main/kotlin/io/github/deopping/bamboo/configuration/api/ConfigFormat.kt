package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.extension

/**
 * Represents a supported configuration file format.
 *
 * Each format defines its recognized file extensions and whether it supports
 * configuration comments.
 *
 * @property extensions the file extensions associated with this format, without leading
 * periods
 * @property supportsComments whether the format supports reading and writing comments
 *
 * @author DeOpping
 * @since 1.0.0
 */
public enum class ConfigFormat(
    public val extensions: Set<String>,
    public val supportsComments: Boolean,
) {
    /**
     * YAML configuration format.
     *
     * Recognized extensions: `.yaml`, `.yml`.
     * @since 1.0.0
     */
    YAML(
        extensions = setOf("yaml", "yml"),
        supportsComments = false,
    ),

    /**
     * JSON configuration format.
     *
     * Recognized extension: `.json`
     * @since 1.0.0
     */
    JSON(
        extensions = setOf("json"),
        supportsComments = false,
    ),

    /**
     * HOCON configuration format.
     *
     * Recognized extensions: `.hocon`, `.conf`.
     * @since 1.0.0
     */
    HOCON(
        extensions = setOf("hocon", "conf"),
        supportsComments = true,
    ),

    /**
     * TOML configuration format.
     *
     * Recognized extension: `.toml`.
     * @since 1.0.0
     */
    TOML(
        extensions = setOf("toml"),
        supportsComments = true,
    ),
    ;

    /**
     * Provides utility methods for working with configuration formats.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Detects the configuration format from a file path.
         *
         * Detection is performed using the file extension, ignoring
         * character case.
         *
         * @param path the configuration file path
         * @return the detected configuration format
         * @throws ConfigException if the path has no file name, has no extension,
         * or uses an unsupported file extension
         * @since 1.0.0
         */
        public fun detect(path: Path): ConfigFormat {
            if (path.fileName == null) {
                throw ConfigException("Cannot detect configuration format for path: $path")
            }

            val extension = path.extension.lowercase(Locale.ROOT)

            if (extension.isBlank()) {
                throw ConfigException(
                    "Cannot detect configuration format for path with blank extension, path: $path",
                )
            }

            return entries.firstOrNull { extension in it.extensions }
                ?: throw ConfigException(
                    "Unsupported configuration file extension: .$extension",
                )
        }
    }
}
