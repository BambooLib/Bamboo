package io.github.deopping.bamboo.world.api

import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Represents a world managed by the server.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface World {
    /**
     * Gets the world's unique identifier.
     * @since 1.0.0
     */
    public val uniqueId: UUID

    /**
     * Gets the world's name.
     * @since 1.0.0
     */
    public val name: String

    /**
     * Gets the minimum build height.
     * @since 1.0.0
     */
    public val minHeight: Int

    /**
     * Gets the maximum build height.
     * @since 1.0.0
     */
    public val maxHeight: Int

    /**
     * Checks whether the world is currently loaded.
     *
     * @return a future containing whether the world is loaded
     * @since 1.0.0
     */
    public fun isLoaded(): CompletableFuture<Boolean>

    /**
     * Checks whether a world position belongs to this world.
     *
     * @param position the position to check
     * @return `true` if the position belongs to this world
     * @since 1.0.0
     */
    public fun containsPosition(position: WorldPositionLike<*>): Boolean = uniqueId == position.worldId

    /**
     * Gets the world's current spawn location.
     *
     * @return a future containing a spawn location
     * @since 1.0.0
     */
    public fun getSpawnLocation(): CompletableFuture<WorldLocation>

    /**
     * Sets the world's spawn location.
     *
     * The location must belong to this world.
     *
     * @param location the new spawn location
     * @return a future containing whether the operation succeeded
     * @throws IllegalArgumentException if the location belongs to another world
     * @since 1.0.0
     */
    public fun setSpawnLocation(location: WorldLocation): CompletableFuture<Boolean>

    /**
     * Gets the world's current time of day.
     *
     * The result is within the range [0, 24000).
     *
     * @return a future containing the current time of day in ticks
     * @since 1.0.0
     */
    public fun getTimeOfDay(): CompletableFuture<Long>

    /**
     * Sets the world's time of day.
     *
     * @param timeOfDay the new time in ticks
     * @return a future containing whether the operation succeeded
     * @throws IllegalArgumentException if timeOfDay is outside [0, 24000)
     * @since 1.0.0
     */
    public fun setTimeOfDay(timeOfDay: Long): CompletableFuture<Boolean>

    /**
     * Gets the world's total elapsed time.
     *
     * @return a future containing the world's full time in ticks
     * @since 1.0.0
     */
    public fun getFullTime(): CompletableFuture<Long>

    /**
     * Sets the world's total elapsed time.
     *
     * @param fullTime the new total elapsed time in ticks
     * @return a future containing whether the operation succeeded
     * @since 1.0.0
     */
    public fun setFullTime(fullTime: Long): CompletableFuture<Boolean>

    /**
     * Gets the world's current weather.
     *
     * @return a future containing the current weather
     * @since 1.0.0
     */
    public fun getWeather(): CompletableFuture<WorldWeather>

    /**
     * Sets the world's weather.
     *
     * @param weather the desired weather
     * @return a future containing whether the operation succeeded
     * @since 1.0.0
     */
    public fun setWeather(weather: WorldWeather): CompletableFuture<Boolean>

    public companion object {
        /**
         * The number of ticks in a Minecraft day.
         * @since 1.0.0
         */
        public const val TICKS_PER_DAY: Long = 24_000L
    }
}
