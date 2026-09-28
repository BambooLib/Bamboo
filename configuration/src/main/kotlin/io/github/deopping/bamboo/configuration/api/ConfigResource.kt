package io.github.deopping.bamboo.configuration.api

import java.io.InputStream

/**
 * Represents a source from which Bamboo can initialize a configuration file.
 *
 * A resource is only used when the target configuration file does not already
 * exist. Its contents are copied to the target file before the configuration
 * is loaded.
 *
 * @author DeOpping
 * @since 0.1.0
 */
fun interface ConfigResource {

    /**
     * Opens the resource for reading.
     *
     * The returned stream is owned by the caller and must be closed after use.
     *
     * @return an input stream containing the resource data
     * @throws ConfigException if the resource cannot be opened
     */
    fun open(): InputStream

    companion object {

        /**
         * Creates a resource backed by a classpath resource.
         *
         * @param path classpath-relative path to the resource
         * @param classLoader class loader used to locate the resource
         * @return a classpath-backed configuration resource
         * @throws ConfigException if the resource does not exist
         */
        @JvmStatic
        fun classpath(
            path: String,
            classLoader: ClassLoader = Thread.currentThread().contextClassLoader
        ): ConfigResource {
            return ConfigResource {
                classLoader.getResourceAsStream(path)
                    ?: throw ConfigException("Configuration resource not found: $path")
            }
        }

    }

}