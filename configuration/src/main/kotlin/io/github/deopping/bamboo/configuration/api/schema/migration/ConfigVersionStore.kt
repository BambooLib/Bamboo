package io.github.deopping.bamboo.configuration.api.schema.migration

import java.nio.file.Path
import java.util.Optional

/**
 * Stores and retrieves configuration schema versions externally.
 *
 * A version store allows configuration versioning to be maintained
 * independently of the configuration file itself.
 *
 * This is useful when the configuration format should not contain
 * version metadata or when version information must be managed
 * by an external storage mechanism.
 *
 * The configuration file path identifies the configuration whose
 * version is being accessed.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigVersionStore {
    /**
     * Retrieves the stored schema version for a configuration.
     *
     * An empty optional indicates that no version has been recorded
     * for the specified configuration.
     *
     * @param path the configuration file path
     * @return the stored schema version, or an empty optional if no version exists
     * @since 1.0.0
     */
    public fun getVersion(path: Path): Optional<Int>

    /**
     * Stores or updates the schema version for a configuration.
     *
     * This operation is performed after the configuration has been
     * successfully updated to the target schema version.
     *
     * @param path the configuration file path
     * @param version the schema version to store
     * @since 1.0.0
     */
    public fun setVersion(
        path: Path,
        version: Int,
    )
}
