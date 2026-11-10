package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.MutableRotation
import io.github.deopping.bamboo.geometry.api.Rotation
import io.github.deopping.bamboo.geometry.api.RotationLike
import io.github.deopping.bamboo.geometry.api.Vector3Like
import io.github.deopping.bamboo.world.internal.WorldMath
import java.util.UUID

/**
 * Represents a mutable location within a world.
 *
 * Mutable world locations use reference equality and are not thread-safe.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class MutableWorldLocation(
    worldId: UUID,
    x: Double,
    y: Double,
    z: Double,
    rotation: RotationLike,
) : WorldLocationLike {
    private var worldIdValue: UUID = worldId
    private var xCoordinate: Double = x
    private var yCoordinate: Double = y
    private var zCoordinate: Double = z

    private val rotationValue: MutableRotation =
        MutableRotation(
            rotation.yaw,
            rotation.pitch,
        )

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

    /**
     * Gets the mutable rotation.
     * @return the location's mutable rotation
     * @since 1.0.0
     */
    public override val rotation: MutableRotation
        get() = rotationValue

    init {
        WorldMath.requireFinite3(x, y, z)
    }

    /**
     * Creates an immutable world location with the specified coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return a new immutable world location
     * @since 1.0.0
     */
    public override fun createPosition(
        x: Double,
        y: Double,
        z: Double,
    ): WorldLocation =
        WorldLocation(
            worldId,
            x,
            y,
            z,
            rotationValue.toImmutable(),
        )

    /**
     * Replaces the world, position, and rotation.
     *
     * All values are validated before any modification occurs.
     *
     * @param worldId the world UUID
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param z the new Z coordinate
     * @param rotation the new rotation
     * @return this mutable location
     * @since 1.0.0
     */
    public fun set(
        worldId: UUID,
        x: Double,
        y: Double,
        z: Double,
        rotation: RotationLike,
    ): MutableWorldLocation {
        WorldMath.requireFinite3(x, y, z)

        val validatedRotation = Rotation(rotation.yaw, rotation.pitch)

        worldIdValue = worldId
        xCoordinate = x
        yCoordinate = y
        zCoordinate = z
        rotationValue.set(validatedRotation)

        return this
    }

    /**
     * Copies all properties from another world location.
     *
     * @param other the location to copy
     * @return this mutable location
     * @since 1.0.0
     */
    public fun set(other: WorldLocationLike): MutableWorldLocation =
        set(
            other.worldId,
            other.x,
            other.y,
            other.z,
            other.rotation,
        )

    /**
     * Copies the world and position from another world position.
     *
     * The existing rotation is preserved.
     *
     * @param position the position to copy
     * @return this mutable location
     * @since 1.0.0
     */
    public fun setPosition(position: WorldPositionLike<*>): MutableWorldLocation =
        set(
            position.worldId,
            position.x,
            position.y,
            position.z,
            rotation,
        )

    /**
     * Copies the rotation from another rotation.
     *
     * @param rotation the rotation to copy
     * @return this mutable location
     * @since 1.0.0
     */
    public fun setRotation(rotation: RotationLike): MutableWorldLocation {
        val validatedRotation = Rotation(rotation.yaw, rotation.pitch)

        rotationValue.set(validatedRotation)

        return this
    }

    /**
     * Moves this location by a displacement in place.
     *
     * The world and rotation are preserved.
     *
     * @param vector the displacement
     * @return this mutable location
     * @since 1.0.0
     */
    public fun moveBy(vector: Vector3Like): MutableWorldLocation =
        set(
            worldId,
            x + vector.x,
            y + vector.y,
            z + vector.z,
            rotation,
        )

    /**
     * Moves this location by a displacement in place.
     *
     * @param vector the displacement
     * @since 1.0.0
     */
    public operator fun plusAssign(vector: Vector3Like) {
        moveBy(vector)
    }

    /**
     * Moves this location by the inverse of a displacement in place.
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
            rotation,
        )
    }

    /**
     * Moves this location to the midpoint between this and another location.
     *
     * The existing rotation is preserved.
     *
     * @param other the other location
     * @return this mutable location
     * @throws IllegalArgumentException if the locations belong to different worlds
     * @since 1.0.0
     */
    public fun midpointAssign(other: WorldLocationLike): MutableWorldLocation {
        WorldMath.requireSameWorld(worldId, other)

        return set(
            worldId,
            x * 0.5 + other.x * 0.5,
            y * 0.5 + other.y * 0.5,
            z * 0.5 + other.z * 0.5,
            rotation,
        )
    }

    /**
     * Interpolates this location toward another location in place.
     *
     * Position coordinates are interpolated. The existing rotation is preserved.
     *
     * @param other the target location
     * @param progress the interpolation progress
     * @return this mutable location
     * @throws IllegalArgumentException if progress is not finite or the locations
     * belong to different worlds
     * @since 1.0.0
     */
    public fun lerpAssign(
        other: WorldLocationLike,
        progress: Double,
    ): MutableWorldLocation {
        WorldMath.requireFinite(progress, "Progress")
        WorldMath.requireSameWorld(worldId, other)

        if (progress == 0.0) {
            return this
        } else if (progress == 1.0) {
            return setPosition(other)
        }

        return set(
            worldId,
            x + (other.x - x) * progress,
            y + (other.y - y) * progress,
            z + (other.z - z) * progress,
            rotation,
        )
    }

    /**
     * Converts this location into an immutable world location.
     *
     * The returned location and its rotation are independent snapshots.
     *
     * @return a new immutable world location
     * @since 1.0.0
     */
    public fun toImmutable(): WorldLocation =
        WorldLocation(
            worldId,
            x,
            y,
            z,
            rotationValue.toImmutable(),
        )
}
