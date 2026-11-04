package io.github.deopping.bamboo.configuration.api

import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import java.lang.AutoCloseable
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * Manages the configuration loaded by Bamboo.
 *
 * A configuration manager maintains the lifecycle of loaded configurations,
 * including loading, unloading, saving, reloading, and closing them.
 *
 * Configurations loaded through the same manager are tracked by their
 * normalized file paths. Loading an already-loaded configuration returns
 * the existing instance.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConfigManager : AutoCloseable {
    /**
     * Loads a configuration from the specified path.
     *
     * If the configuration has already been loaded by this manager, the
     * existing instance is returned.
     *
     * @param path the configuration file path
     * @return the loaded configuration
     * @throws ConfigException if the configuration cannot be loaded
     * @since 1.0.0
     */
    public fun load(path: Path): Config

    /**
     * Loads a configuration using the specified loading options.
     *
     * If the configuration has already been loaded by this manager, the
     * existing instance is returned and the supplied options are not applied
     * again.
     *
     * @param path the configuration file path
     * @param options the options controlling configuration initialization and schema
     * application
     * @return the loaded configuration
     * @throws ConfigException if the configuration cannot be loaded or updated
     * @since 1.0.0
     */
    public fun load(
        path: Path,
        options: ConfigLoadOptions,
    ): Config

    /**
     * Loads a configuration asynchronously.
     *
     * The loading operation is submitted to the supplied executor.
     *
     * @param path the configuration file path
     * @param executor the executor used to perform the loading operation
     * @return a future containing the loaded configuration
     * @since 1.0.0
     */
    public fun loadAsync(
        path: Path,
        executor: Executor,
    ): CompletableFuture<Config>

    /**
     * Loads a configuration asynchronously using the specified loading options.
     *
     * The loading operation is submitted to the supplied executor.
     *
     * @param path the configuration file path
     * @param options the options controlling the configuration initialization and schema
     * application
     * @param executor the executor used to perform the loading operation
     * @return a future containing the loaded configuration
     * @since 1.0.0
     */
    public fun loadAsync(
        path: Path,
        options: ConfigLoadOptions,
        executor: Executor,
    ): CompletableFuture<Config>

    /**
     * Finds a configuration already loaded by this manager.
     *
     * This method does not attempt to load the configuration when it is
     * not already managed.
     *
     * @param path the configuration file path
     * @return the loaded configuration, or an empty optional if none is found
     * @since 1.0.0
     */
    public fun find(path: Path): Optional<Config>

    /**
     * Unloads a configuration from this manager.
     *
     * The configuration is removed from the manager and closed. Any
     * unsaved changes are handled by the configuration's closing behavior.
     * If the path is not managed by this manager, this operation has no effect.
     *
     * @param path the configuration file path
     * @throws ConfigException if the configuration cannot be closed
     * @since 1.0.0
     */
    public fun unload(path: Path)

    /**
     * Saves all configurations currently managed by this manager.
     *
     * The manager attempts to save every loaded configuration, even when
     * an earlier save operation fails.
     *
     * @throws ConfigException if one or more configurations cannot be saved
     * @since 1.0.0
     */
    public fun saveAll()

    /**
     * Saves all managed configurations asynchronously.
     *
     * The saving operation is submitted to the supplied executor.
     *
     * @param executor the executor used to perform the saving operation
     * @return a future completed when all save operations have finished
     * @since 1.0.0
     */
    public fun saveAllAsync(executor: Executor): CompletableFuture<Void>

    /**
     * Reloads configurations that have been modified externally.
     *
     * Configurations with unsaved local changes are not reloaded.
     * The manager attempts to check every loaded configuration, even when
     * an earlier loaded operation fails.
     *
     * @return the number of configurations successfully reloaded
     * @throws ConfigException if one or more configurations cannot be checked or reloaded
     * @since 1.0.0
     */
    public fun reloadChanged(): Int

    /**
     * Reloads externally modified configurations asynchronously.
     *
     * The reload operation is submitted to the supplied executor.
     *
     * @param executor the executor used to perform the reload operation
     * @return a future containing the number of configurations successfully reloaded
     * @since 1.0.0
     */
    public fun reloadChangedAsync(executor: Executor): CompletableFuture<Int>

    /**
     * Closes this manager and all configurations it manages.
     *
     * Closing a manager releases its managed resources and prevents further
     * operations. Each managed configuration is given the opportunity to
     * close, even if another configuration fails to close.
     *
     * @throws ConfigException if one or more managed configurations cannot be closed
     * @since 1.0.0
     */
    override fun close()
}
