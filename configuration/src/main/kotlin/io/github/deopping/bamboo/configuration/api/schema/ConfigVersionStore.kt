package io.github.deopping.bamboo.configuration.api.schema

import java.nio.file.Path

/**
 * Provides persistence storage for configuration schema versions that are not
 * stored inside the configuration itself.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigVersionStore {

    /**
     * Retrieves the stored schema version for a configuration.
     *
     * @param path configuration file path
     * @return stored version, or `null` when no version exists
     */
    fun getVersion(path: Path): Int?

    /**
     * Stores a configuration schema version.
     *
     * @param path configuration file path
     * @param version schema version to store
     */
    fun setVersion(path: Path, version: Int)

}