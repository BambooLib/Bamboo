package io.github.deopping.bamboo.configuration.api.schema.migration

/**
 * Defines how a configuration's schema version is stored and retrieved.
 *
 * Configuration versioning allows Bamboo to determine which migrations
 * must be applied when updating a configuration to a newer schema version.
 *
 * Implementations can store the version directly inside the configuration
 * or delegate a version storage to an external [ConfigVersionStore].
 *
 * @property version the target schema version
 *
 * @author DeOpping
 * @since 1.0.0
 */
public sealed interface ConfigVersioning {
    /**
     * Gets the target schema version.
     *
     * This is the version that a configuration must reach after all
     * required migrations have been applied.
     *
     * @return the target schema version.
     * @since 1.0.0
     */
    public val version: Int

    /**
     * Stores the configuration version inside the configuration file.
     *
     * The version is read from and written to the configuration at [path].
     *
     * @property version the target schema version
     * @property path the configuration path used to store the version
     * @since 1.0.0
     */
    public data class Embedded(
        override val version: Int,
        val path: String,
    ) : ConfigVersioning {
        init {
            require(version >= 0) {
                "Configuration version cannot be negative."
            }

            require(path.isNotBlank()) {
                "Configuration version path cannot be blank."
            }
        }
    }

    /**
     * Stores the configuration version outside the configuration file.
     *
     * The supplied [store] is responsible for retrieving and updating
     * the version associated with the configuration's file path.
     *
     * @property version the target schema version
     * @property store the store used to retrieve and update configuration versions
     * @since 1.0.0
     */
    public data class External(
        override val version: Int,
        val store: ConfigVersionStore,
    ) : ConfigVersioning {
        init {
            require(version >= 0) {
                "Configuration version cannot be negative."
            }
        }
    }
}
