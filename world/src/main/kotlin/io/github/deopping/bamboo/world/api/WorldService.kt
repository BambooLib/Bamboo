package io.github.deopping.bamboo.world.api

import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Provides access to worlds managed by the server.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface WorldService {
    /**
     * Gets a loaded world by its unique identifier.
     *
     * @param uniqueId the world's UUID
     * @return a future containing the world, or an empty optional if it is not loaded
     * @since 1.0.0
     */
    public fun getWorld(uniqueId: UUID): CompletableFuture<Optional<World>>

    /**
     * Gets a loaded world by its name.
     *
     * @param world the world name
     * @return a future containing the world, or an empty optional if it is not loaded
     * @since 1.0.0
     */
    public fun getWorld(world: String): CompletableFuture<Optional<World>>

    /**
     * Gets a snapshot of all currently loaded worlds.
     *
     * @return a future containing the currently loaded worlds
     * @since 1.0.0
     */
    public fun getWorlds(): CompletableFuture<Collection<World>>
}
