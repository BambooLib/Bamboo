package io.github.deopping.bamboo.configuration.api.schema

/**
 * Defines how a configuration's schema version is stored.
 */
sealed interface ConfigVersioning {

    val version: Int

    /**
     * Stores the version inside the configuration itself.
     *
     * @param version current schema version
     * @param path configuration path containing the version
     */
    data class Embedded(
        override val version: Int,
        val path: String
    ) : ConfigVersioning

    /**
     * Stores the version externally.
     *
     * @param version current schema version
     * @param store external version store
     */
    data class External(
        override val version: Int,
        val store: ConfigVersionStore
    ) : ConfigVersioning

}