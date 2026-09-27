package io.github.deopping.bamboo.configuration.api

import java.lang.AutoCloseable
import java.nio.file.Path

/**
 * Manages the lifecycle of Bamboo configuration files.
 *
 * A configuration manager keeps track of the configurations loaded through
 * it and provides operations for loading, unloading, saving, and reloading
 * those configurations.
 *
 * A manager may optionally have a parent directory. When present, relative
 * paths passed to [load], [unload], and [get] are resolved relative to that
 * directory. Absolute paths are used as-is.
 *
 * Loading the same normalized path more than once returns the already
 * managed configuration rather than creating another instance.
 *
 * Managers are independent of one another. A configuration loaded by one
 * manager is not automatically visible to another manager.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ConfigManager : AutoCloseable {

    /**
     * Loads and begins managing a configuration file.
     *
     * The configuration format is determined from the file extension.
     *
     * Supported formats are `YAML`, `JSON`, `HOCON`, and `TOML`.
     *
     * If the normalized path is already managed by this manager, the
     * existing configuration is returned.
     *
     * @param path path to the configuration file
     * @return the managed [Config]
     * @throws ConfigException if the file format cannot be determined or
     * the configuration cannot be loaded
     * @since 0.1.0
     */
    fun load(path: Path): Config

    /**
     * Stops managing a configuration.
     *
     * If the specified path is not currently managed, this method has no
     * effect.
     *
     * Unloading a configuration does not implicitly save unsaved changes.
     * Call [Config.save] before unloading when those changes should be
     * persisted.
     *
     * @param path path to the configuration
     * @since 0.1.0
     */
    fun unload(path: Path)

    /**
     * Returns the configuration currently managed for the specified path.
     *
     * This method never loads a configuration. If the path is not currently
     * managed, `null` is returned.
     *
     * @param path path to the configuration
     * @return the managed configuration, or `null` if it is not loaded
     * @since 0.1.0
     */
    fun get(path: Path): Config?

    /**
     * Saves all configurations currently managed by this manager.
     *
     * Only configurations with pending changes need to write their contents
     * to disk.
     *
     * @throws ConfigException if a configuration cannot be saved
     * @since 0.1.0
     */
    fun saveAll()

    /**
     * Reloads managed configurations whose underlying files have changed.
     *
     * Configurations that have not changed are left untouched. A
     * configuration with unsaved in-memory changes is not automatically
     * overwritten by an external file change.
     *
     * @return the number of configurations that were reloaded
     * @throws ConfigException if a changed configuration cannot be reloaded
     * @since 0.1.0
     */
    fun reloadChanged(): Int

    /**
     * Closes this manager and all configurations currently managed by it.
     *
     * Once closed, the manager should no loner be used.
     *
     * @since 0.1.0
     */
    override fun close()

}