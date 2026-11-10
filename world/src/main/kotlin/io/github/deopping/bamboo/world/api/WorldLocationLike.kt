package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Rotation
import io.github.deopping.bamboo.geometry.api.RotationLike

/**
 * Provides read-only access to a location within a world.
 *
 * A world location consists of a world position and a rotation.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface WorldLocationLike : WorldPositionLike<WorldLocation> {
    /**
     * Gets the location's rotation.
     * @since 1.0.0
     */
    public val rotation: RotationLike

    /**
     * Creates an immutable world location with the specified coordinates.
     *
     * The current world and rotation are preserved.
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
    ): WorldLocation = WorldLocation(worldId, x, y, z, Rotation(rotation.yaw, rotation.pitch))

    /**
     * Converts this location into an immutable world location.
     *
     * @return an independent immutable snapshot
     * @since 1.0.0
     */
    public fun toWorldLocation(): WorldLocation = createPosition(x, y, z)

    /**
     * Creates a location with a different rotation.
     *
     * @param rotation the new rotation
     * @return a new immutable world location
     * @since 1.0.0
     */
    public fun withRotation(rotation: RotationLike): WorldLocation =
        WorldLocation(
            worldId,
            x,
            y,
            z,
            Rotation(rotation.yaw, rotation.pitch),
        )
}
