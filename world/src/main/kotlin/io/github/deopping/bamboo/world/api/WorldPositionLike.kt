package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Position3
import io.github.deopping.bamboo.geometry.api.Position3Like
import io.github.deopping.bamboo.geometry.api.Vector3
import io.github.deopping.bamboo.world.internal.WorldMath
import java.util.UUID

/**
 * Provides read-only access to a position within a world
 * and common world-position operations.
 *
 * Operations involving two positions require both positions to
 * belong to the same world.
 *
 * @param P the immutable result type produced by position operations
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface WorldPositionLike<P : WorldPositionLike<P>> : Position3Like<P> {
    /**
     * Gets the world's unique identifier.
     * @since 1.0.0
     */
    public val worldId: UUID

    private fun requireSameWorld(other: Position3Like<*>) {
        WorldMath.requireSameWorld(worldId, other)
    }

    /**
     * Calculates the squared distance to another world position.
     *
     * @param other the other position
     * @return the squared distance
     * @throws IllegalArgumentException if the positions belong to different worlds
     * @since 1.0.0
     */
    public override fun distanceSquaredTo(other: Position3Like<*>): Double {
        requireSameWorld(other)
        return super<Position3Like>.distanceSquaredTo(other)
    }

    /**
     * Calculates the distance to another world position.
     *
     * @param other the other position
     * @return the distance between positions
     * @throws IllegalArgumentException if the positions belong to different worlds
     * @since 1.0.0
     */
    public override fun distanceTo(other: Position3Like<*>): Double {
        requireSameWorld(other)
        return super<Position3Like>.distanceTo(other)
    }

    /**
     * Calculates the midpoint between this position and another.
     *
     * @param other the other position
     * @return the midpoint as an immutable world position or location
     * @throws IllegalArgumentException if the positions belong to different worlds
     * @since 1.0.0
     */
    public override fun midpoint(other: Position3Like<*>): P {
        requireSameWorld(other)
        return super<Position3Like>.midpoint(other)
    }

    /**
     * Calculates the displacement from this position to another.
     *
     * @param other the target position
     * @return the displacement vector
     * @throws IllegalArgumentException if the positions belong to different worlds
     * @since 1.0.0
     */
    public override fun vectorTo(other: Position3Like<*>): Vector3 {
        requireSameWorld(other)
        return super<Position3Like>.vectorTo(other)
    }

    /**
     * Calculates the displacement between this position and another.
     *
     * @param other the other position
     * @return the resulting immutable vector
     * @throws IllegalArgumentException if the positions belong to different worlds
     * @since 1.0.0
     */
    public override operator fun minus(other: Position3Like<*>): Vector3 {
        requireSameWorld(other)
        return super<Position3Like>.minus(other)
    }

    /**
     * Interpolates between this position and another.
     *
     * Values outside the range [0, 1] produce extrapolated positions.
     *
     * @param other the target position
     * @param progress the interpolation progress
     * @return the resulting immutable world position or location
     * @throws IllegalArgumentException  of the progress is not finite or the positions
     * belong to different worlds
     * @since 1.0.0
     */
    public override fun lerp(
        other: Position3Like<*>,
        progress: Double,
    ): P {
        requireSameWorld(other)
        return super<Position3Like>.lerp(other, progress)
    }

    /**
     * Converts this world position into a geometry position.
     *
     * The resulting position no longer contains world identity.
     *
     * @return an immutable geometry position
     * @since 1.0.0
     */
    public fun toPosition3(): Position3 = Position3(x, y, z)
}
