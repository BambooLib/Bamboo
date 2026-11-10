package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Rotation
import io.github.deopping.bamboo.geometry.api.RotationLike
import io.github.deopping.bamboo.world.internal.WorldMath
import java.util.UUID

/**
 * Represents an immutable location within a world.
 *
 * @property worldId the world's UUID
 * @property x the X coordinate
 * @property y the Y coordinate
 * @property z the Z coordinate
 * @property rotation the location's rotation
 *
 * @author DeOpping
 * @since 1.0.0
 */
public data class WorldLocation(
    public override val worldId: UUID,
    public override val x: Double,
    public override val y: Double,
    public override val z: Double,
    public override val rotation: Rotation,
) : WorldLocationLike {
    init {
        WorldMath.requireFinite3(x, y, z)
    }

    /**
     * Creates an immutable location from a world position and rotation.
     *
     * The rotation is copied to ensure the resulting location
     * remains independent of mutable inputs.
     *
     * @param position the world position
     * @param rotation the location's rotation
     * @since 1.0.0
     */
    public constructor(
        position: WorldPositionLike<*>,
        rotation: RotationLike,
    ) : this(
        position.worldId,
        position.x,
        position.y,
        position.z,
        Rotation(rotation.yaw, rotation.pitch),
    )

    /**
     * Creates an immutable location with the specified coordinates.
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
    ): WorldLocation = WorldLocation(worldId, x, y, z, rotation)

    /**
     * Converts this location into a mutable world location.
     *
     * @return a new mutable location with the same properties
     * @since 1.0.0
     */
    public fun toMutable(): MutableWorldLocation =
        MutableWorldLocation(
            worldId,
            x,
            y,
            z,
            rotation,
        )
}
