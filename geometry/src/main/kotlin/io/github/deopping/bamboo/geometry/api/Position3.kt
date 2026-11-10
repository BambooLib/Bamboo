package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.GeometryMath

/**
 * Represents an immutable position in three-dimensional space.
 *
 * @property x the X coordinate
 * @property y the Y coordinate
 * @property z the Z coordinate
 * @author DeOpping
 * @since 1.0.0
 */
public data class Position3(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Position3Like<Position3> {
    init {
        GeometryMath.requireFinite3(x, y, z, "coordinate")
    }

    override fun createPosition(
        x: Double,
        y: Double,
        z: Double,
    ): Position3 = Position3(x, y, z)

    /**
     * Converts this position into a mutable position.
     *
     * @return a new mutable position with the same coordinates
     * @since 1.0.0
     */
    public fun toMutable(): MutablePosition3 = MutablePosition3(x, y, z)

    /**
     * Contains constants and factory methods for immutable positions.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Represents the origin of three-dimensional space.
         * @since 1.0.0
         */
        public val ORIGIN: Position3 = Position3(0.0, 0.0, 0.0)
    }
}
