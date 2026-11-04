package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import java.io.InputStream

/**
 * Represents a resource that can be used to initialize a configuration.
 *
 * A resource provides an input stream containing the resource's contents.
 * Resources can be supplied directly or created from the classpath.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public fun interface ConfigResource {
    /**
     * Opens the resource for reading.
     *
     * Each invocation should provide a new input stream positioned at the
     * beginning of the resource.
     *
     * The caller is responsible for closing the returned stream.
     *
     * @return an input stream containing the resource's contents
     * @throws ConfigException if the resource cannot be opened
     * @since 1.0.0
     */
    public fun open(): InputStream

    /**
     * Provides factory methods for creating configuration resources.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Creates a configuration resource from a classpath.
         *
         * The supplied path is interpreted as a classpath resource name.
         * A single leading slash is removed before the resource is resolved.
         *
         * When no class loader is supplied, the current thread's context
         * class loader is used. If it is unavailable, the system class loader
         * is used instead
         *
         * @param path the classpath resource path
         * @param classLoader the optional class loader used to locate the resource
         * @return a configuration resource backed by the classpath
         * @throws IllegalArgumentException if the resource path is blank
         * @throws ConfigException if the resource cannot be found or no class loader is available
         * @since 1.0.0
         */
        @JvmStatic
        public fun classpath(
            path: String,
            classLoader: ClassLoader? = Thread.currentThread().contextClassLoader,
        ): ConfigResource {
            require(path.isNotBlank()) {
                "Configuration resource path cannot be blank."
            }

            val normalizedPath = path.removePrefix("/")
            val effectiveClassLoader =
                classLoader ?: ClassLoader.getSystemClassLoader()
                    ?: throw ConfigException(
                        "No class loader is available for resource '$path'.",
                    )

            return ConfigResource {
                effectiveClassLoader.getResourceAsStream(normalizedPath)
                    ?: throw ConfigException(
                        "Configuration resource not found: $normalizedPath",
                    )
            }
        }
    }
}
