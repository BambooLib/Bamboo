package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.GeometryMath

/**
 * Represents an immutable block position in three-dimensional integer space.
 *
 * The coordinates identify the lower corner of the represented block.
 *
 * @property x the X block coordinate
 * @property y the Y block coordinate
 * @property z the Z block coordinate
 * @author DeOpping
 * @since 1.0.0
 */
public data class BlockPosition(
    override val x: Int,
    override val y: Int,
    override val z: Int,
) : BlockPositionLike {
    /**
     * Converts this block position into a mutable block position.
     *
     * @return a new mutable block position with the same coordinates
     * @since 1.0.0
     */
    public fun toMutable(): MutableBlockPosition = MutableBlockPosition(x, y, z)

    /**
     * Contains constants and factory methods for immutable block positions.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Represents the origin of block-coordinate space.
         * @since 1.0.0
         */
        public val ORIGIN: BlockPosition = BlockPosition(0, 0, 0)

        /**
         * Creates a block position from continuous position.
         *
         * Each coordinate is rounded down to the containing block coordinate.
         *
         * @param position the continuous position to convert
         * @return the corresponding immutable block position
         * @throws IllegalArgumentException if any coordinate is non-finite
         * or outside the Int range after flooring
         * @since 1.0.0
         */
        public fun fromPosition(position: Position3Like): BlockPosition =
            BlockPosition(
                GeometryMath.floorToInt(position.x, "X coordinate"),
                GeometryMath.floorToInt(position.y, "Y coordinate"),
                GeometryMath.floorToInt(position.z, "Z coordinate"),
            )
    }
}
