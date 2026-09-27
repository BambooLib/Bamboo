package io.github.deopping.bamboo.configuration.api

import java.nio.file.Path

/**
 * Represents a configuration file format supported by Bamboo.
 *
 * Each format defines the file extensions recognized by Bamboo and whether
 * the format supports configuration comments.
 *
 * @author DeOpping
 * @since 0.1.0
 */
enum class ConfigFormat(
    /**
     * File extensions recognized for this format.
     *
     * Extensions are stored without the leading period.
     *
     * @since 0.1.0
     */
    val extensions: Set<String>,

    /**
     * Whether this configuration format supports comments.
     *
     * @since 0.1.0
     */
    val supportsComments: Boolean
) {

    /**
     * YAML configuration format.
     *
     * Recognized extensions: `.yml`, `.yaml`
     *
     * NightConfig's SnakeYAML parser does not preserve comments.
     *
     * @since 0.1.0
     */
    YAML(
        extensions = setOf("yml", "yaml"),
        supportsComments = false
    ),

    /**
     * JSON configuration format.
     *
     * Recognized extension: `.json`
     *
     * Standard JSON does not support comments.
     *
     * @since 0.1.0
     */
    JSON(
        extensions = setOf("json"),
        supportsComments = false
    ),

    /**
     * HOCON configuration format.
     *
     * Recognized extensions: `.conf`, `.hocon`
     *
     * @since 0.1.0
     */
    HOCON(
        extensions = setOf("conf", "hocon"),
        supportsComments = true
    ),

    /**
     * TOML configuration format.
     *
     * Recognized extension: `.toml`
     *
     * @since 0.1.0
     */
    TOML(
        extensions = setOf("toml"),
        supportsComments = true
    );

    companion object {

        /**
         * Determines the configuration format from a file's extension.
         *
         * Extension matching is case-insensitive. For example,
         * `config.YML` and `config.yml` are both detected as [YAML].
         *
         * @param path configuration file path
         * @return the format associated with the file extension
         * @throws ConfigException if the file extension is not supported
         * @since 0.1.0
         */
        fun detect(path: Path): ConfigFormat {
            val extension = path.fileName
                .toString()
                .substringAfterLast('.', "")
                .lowercase()

            return entries.firstOrNull { extension in it.extensions }
                ?: throw ConfigException(
                    "Unsupported configuration file extension: .$extension"
                )
        }

    }

}