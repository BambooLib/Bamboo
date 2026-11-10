package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Position3Like
import io.github.deopping.bamboo.geometry.api.Vector3Like
import io.github.deopping.bamboo.world.internal.WorldMath
import java.util.UUID

/**
 * Represents a mutable position within a world.
 *
 * Mutable world positions use reference equality and are not thread-safe.
 *
 * @property worldId the world's UUID
 * @property x the X coordinate
 * @property y the Y coordinate
 * @property z the Z coordinate
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class MutableWorldPosition(
    worldId: UUID,
    x: Double,
    y: Double,
    z: Double,
) : WorldPositionLike<WorldPosition> {
    private var worldIdValue: UUID = worldId
    private var xCoordinate: Double = x
    private var yCoordinate: Double = y
    private var zCoordinate: Double = z

    /**
     * Gets or sets the world's UUID.
     * @since 1.0.0
     */
    public override var worldId: UUID
        get() = worldIdValue
        set(value) {
            worldIdValue = value
        }

    /**
     * Gets or sets the X coordinate.
     * @since 1.0.0
     */
    public override var x: Double
        get() = xCoordinate
        set(value) {
            WorldMath.requireFinite(value, "X coordinate")
            xCoordinate = value
        }

    /**
     * Gets or sets the Y coordinate.
     * @since 1.0.0
     */
    public override var y: Double
        get() = yCoordinate
        set(value) {
            WorldMath.requireFinite(value, "Y coordinate")
            yCoordinate = value
        }

    /**
     * Gets or sets the Z coordinate.
     * @since 1.0.0
     */
    public override var z: Double
        get() = zCoordinate
        set(value) {
            WorldMath.requireFinite(value, "Z coordinate")
            zCoordinate = value
        }

    init {
        WorldMath.requireFinite3(x, y, z)
    }

    public override fun createPosition(
        x: Double,
        y: Double,
        z: Double,
    ): WorldPosition = WorldPosition(worldId, x, y, z)

    /**
     * Replaces the world and all position coordinates.
     *
     * All coordinates are validated before any modification occurs.
     *
     * @param worldId the new world UUID
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param z the new Z coordinate
     * @return this mutable position
     * @since 1.0.0
     */
    public fun set(
        worldId: UUID,
        x: Double,
        y: Double,
        z: Double,
    ): MutableWorldPosition {
        WorldMath.requireFinite3(x, y, z)

        worldIdValue = worldId
        xCoordinate = x
        yCoordinate = y
        zCoordinate = z

        return this
    }

    /**
     * Copies the world and coordinates from another world position.
     *
     * @param other the position to copy
     * @return this mutable position
     * @since 1.0.0
     */
    public fun set(other: WorldPositionLike<*>): MutableWorldPosition = set(other.worldId, other.x, other.y, other.z)

    /**
     * Copies coordinates from a geometry position.
     *
     * @param worldId the world's UUID
     * @param position the geometry position
     * @return this mutable position
     * @since 1.0.0
     */
    public fun set(
        worldId: UUID,
        position: Position3Like<*>,
    ): MutableWorldPosition = set(worldId, position.x, position.y, position.z)

    /**
     * Moves this position by a displacement in place.
     *
     * @param vector the displacement
     * @since 1.0.0
     */
    public operator fun plusAssign(vector: Vector3Like) {
        set(
            worldId,
            x + vector.x,
            y + vector.y,
            z + vector.z,
        )
    }

    /**
     * Moves this position by the inverse of a displacement in place.
     *
     * @param vector the displacement
     * @since 1.0.0
     */
    public operator fun minusAssign(vector: Vector3Like) {
        set(
            worldId,
            x - vector.x,
            y - vector.y,
            z - vector.z,
        )
    }

    /**
     * Moves this position to the midpoint between this and another position.
     *
     * @param other the other position
     * @return this mutable position
     * @throws IllegalArgumentException if the positions belong to different worlds
     * @since 1.0.0
     */
    public fun midpointAssign(other: WorldPositionLike<*>): MutableWorldPosition {
        WorldMath.requireSameWorld(worldId, other)

        return set(
            worldId,
            x * 0.5 + other.x * 0.5,
            y * 0.5 + other.y * 0.5,
            z * 0.5 + other.z * 0.5,
        )
    }

    /**
     * Interpolates this position toward another position in place.
     *
     * Values outside the range [0, 1] produce extrapolated positions.
     *
     * @param other the target position
     * @param progress the interpolation progress
     * @throws IllegalArgumentException if progress is not finite or the positions
     * belong to different worlds
     * @since 1.0.0
     */
    public fun lerpAssign(
        other: WorldPositionLike<*>,
        progress: Double,
    ): MutableWorldPosition {
        WorldMath.requireFinite(progress, "Progress")
        WorldMath.requireSameWorld(worldId, other)

        if (progress == 0.0) {
            return this
        } else if (progress == 1.0) {
            return set(other)
        }

        return set(
            worldId,
            x + (other.x - x) * progress,
            y + (other.y - y) * progress,
            z + (other.z - z) * progress,
        )
    }

    /**
     * Converts this position into an immutable world position.
     *
     * The returned position is an independent snapshot.
     *
     * @return a new immutable world position
     * @since 1.0.0
     */
    public fun toImmutable(): WorldPosition = WorldPosition(worldId, x, y, z)
}
