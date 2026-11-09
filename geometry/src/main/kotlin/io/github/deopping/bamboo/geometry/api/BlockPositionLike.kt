package io.github.deopping.bamboo.geometry.api

import kotlin.math.hypot

/**
 * Provides read-only access to three-dimensional integer block coordinates
 * and common block position operations.
 *
 * Block coordinates identify the lower corner of a block in continuous space.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface BlockPositionLike {
    /**
     * Gets the X block coordinate.
     * @since 1.0.0
     */
    public val x: Int

    /**
     * Gets the Y block coordinate.
     * @since 1.0.0
     */
    public val y: Int

    /**
     * Gets the Z block coordinate.
     * @since 1.0.0
     */
    public val z: Int

    /**
     * Calculates the squared distance to another block position.
     *
     * The result is expressed as a Double to avoid integer overflow when
     * subtracting or squaring extreme Int coordinates.
     *
     * @param other the other block position
     * @return the squared distance
     * @since 1.0.0
     */
    public fun distanceSquaredTo(other: BlockPositionLike): Double {
        val dx = other.x.toDouble() - x
        val dy = other.y.toDouble() - y
        val dz = other.z.toDouble() - z
        return dx * dx + dy * dy + dz * dz
    }

    /**
     * Calculates the distance to another block position.
     *
     * Uses a numerically stable hypotenuse calculation.
     *
     * @param other the other block position
     * @return the distance between position
     * @since 1.0.0
     */
    public fun distanceTo(other: BlockPositionLike): Double {
        val dx = other.x.toDouble() - x
        val dy = other.y.toDouble() - y
        val dz = other.z.toDouble() - z
        return hypot(hypot(dx, dy), dz)
    }

    /**
     * Calculates the midpoint between this block position and another.
     *
     * The result is a continuous position because the midpoint may have
     * fractional coordinates.
     *
     * @param other the other block position
     * @return the midpoint as an immutable position
     * @since 1.0.0
     */
    public fun midpoint(other: BlockPositionLike): Position3 =
        Position3(
            x * 0.5 + other.x * 0.5,
            y * 0.5 + other.y * 0.5,
            z * 0.5 + other.z * 0.5,
        )

    /**
     * Calculates the displacement from this block position to another.
     *
     * @param other the target block position
     * @return the displacement vector
     * @since 1.0.0
     */
    public fun vectorTo(other: BlockPositionLike): Vector3 =
        Vector3(
            other.x.toDouble() - x,
            other.y.toDouble() - y,
            other.z.toDouble() - z,
        )

    /**
     * Calculates the displacement between this block position and another.
     *
     * The result follows the same direction convention as subtraction
     * between two continuous positions: this position minus the other.
     *
     * @param other the other block position
     * @return the displacement vector
     * @since 1.0.0
     */
    public operator fun minus(other: BlockPositionLike): Vector3 =
        Vector3(
            x.toDouble() - other.x,
            y.toDouble() - other.y,
            z.toDouble() - other.z,
        )

    /**
     * Creates an immutable block position offset from this position.
     *
     * Integer overflow is checked rather than silently wrapping coordinates.
     *
     * @param dx the X offset
     * @param dy the Y offset
     * @param dz the Z offset
     * @return the resulting immutable block position
     * @throws ArithmeticException if any resulting coordinate overflows
     * @since 1.0.0
     */
    public fun offset(
        dx: Int,
        dy: Int,
        dz: Int,
    ): BlockPosition =
        BlockPosition(
            Math.addExact(x, dx),
            Math.addExact(y, dy),
            Math.addExact(z, dz),
        )

    /**
     * Converts this block position into an immutable continuous position
     * representing the block's lower corner.
     *
     * @return the corresponding immutable position
     * @since 1.0.0
     */
    public fun toPosition(): Position3 = Position3(x.toDouble(), y.toDouble(), z.toDouble())

    /**
     * Calculates the geometric center of the block represented by this position.
     *
     * @return the block center as an immutable position
     * @since 1.0.0
     */
    public fun center(): Position3 =
        Position3(
            x.toDouble() + 0.5,
            y.toDouble() + 0.5,
            z.toDouble() + 0.5,
        )

    /**
     * Checks whether this block position represents the origin.
     *
     * @return `true` if all coordinates are zero
     * @since 1.0.0
     */
    public val isOrigin: Boolean
        get() = x == 0 && y == 0 && z == 0
}
