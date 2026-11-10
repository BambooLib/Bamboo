package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Position3Like
import io.github.deopping.bamboo.world.internal.WorldMath
import java.util.UUID

/**
 * Represents an immutable position within a world.
 *
 * @property worldId the world's UUID
 * @property x the X coordinate
 * @property y the Y coordinate
 * @property z the Z coordinate
 *
 * @author DeOpping
 * @since 1.0.0
 */
public data class WorldPosition(
    public override val worldId: UUID,
    public override val x: Double,
    public override val y: Double,
    public override val z: Double,
) : WorldPositionLike<WorldPosition> {
    init {
        WorldMath.requireFinite3(x, y, z)
    }

    /**
     * Creates an immutable world position from a geometry position.
     *
     * @param worldId the world's UUID
     * @param position the geometry position
     * @since 1.0.0
     */
    public constructor(
        worldId: UUID,
        position: Position3Like<*>,
    ) : this(worldId, position.x, position.y, position.z)

    public override fun createPosition(
        x: Double,
        y: Double,
        z: Double,
    ): WorldPosition = WorldPosition(worldId, x, y, z)

    /**
     * Converts this position into a mutable world position.
     *
     * @return a new mutable position with the same world and coordinates.
     * @since 1.0.0
     */
    public fun toMutable(): MutableWorldPosition = MutableWorldPosition(worldId, x, y, z)

    public companion object {
        /**
         * Creates an immutable world position at the world's origin.
         *
         * @param worldId the world's UUID
         * @return the world's origin position
         * @since 1.0.0
         */
        public fun origin(worldId: UUID): WorldPosition = WorldPosition(worldId, 0.0, 0.0, 0.0)
    }
}
