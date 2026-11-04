package io.github.deopping.bamboo.configuration.api.backends

import io.github.deopping.bamboo.configuration.api.ConfigFormat
import java.nio.file.Path

/**
 * Creates configuration backends for specific files and formats.
 *
 * A backend factory allows Bamboo's configuration system to obtain the
 * appropriate storage implementation for a configuration file.
 *
 * Implementations may provide custom configuration backends, allowing
 * additional storage formats or alternative storage implementations to
 * integrate with Bamboo's configuration system.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public fun interface ConfigBackendFactory {
    /**
     * Creates a configuration backend for the specified file and format.
     *
     * The returned backend is managed by the configuration system, which
     * is responsible for loading and eventually closing it.
     *
     * The factory should configure the backend for the supplied path and
     * format but should not load the configuration data itself.
     *
     * @param path the configuration file path
     * @param format the format used to read and write the configuration
     * @return a new configuration backend
     * @since 1.0.0
     */
    public fun create(
        path: Path,
        format: ConfigFormat,
    ): ConfigBackend
}
