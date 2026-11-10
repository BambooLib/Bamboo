package io.github.deopping.bamboo.world.internal

import io.github.deopping.bamboo.geometry.api.Position3Like
import io.github.deopping.bamboo.world.api.WorldPositionLike
import java.util.UUID

/**
 * Provides internal validation utilities for world-related types.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object WorldMath {
    fun requireFinite(
        value: Double,
        name: String,
    ) {
        require(value.isFinite()) {
            "$name must be finite."
        }
    }

    fun requireFinite3(
        x: Double,
        y: Double,
        z: Double,
        name: String = "Coordinate",
    ) {
        requireFinite(x, "$name X")
        requireFinite(y, "$name Y")
        requireFinite(z, "$name Z")
    }

    fun requireSameWorld(
        worldId: UUID,
        other: Position3Like<*>,
    ) {
        require(other is WorldPositionLike<*> && worldId == other.worldId) {
            "Positions must belong to the same world."
        }
    }
}
