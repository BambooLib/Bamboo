package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.math.CoordinateMath
import io.github.deopping.bamboo.geometry.internal.math.InterpolationMath
import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation

/**
 * Provides read-only access to three-dimensional position coordinates
 * and common position operations.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface Position3Like<P : Position3Like<P>> : Coordinate3Like {
    /**
     * Creates an immutable position with the specified coordinates.
     *
     * @return a new immutable position
     */
    public fun createPosition(
        x: Double,
        y: Double,
        z: Double,
    ): P

    /**
     * Calculates the squared distance to another position.
     *
     * @param other the other position
     * @return the squared distance
     * @since 1.0.0
     */
    public fun distanceSquaredTo(other: Position3Like<*>): Double = CoordinateMath.distanceSquared(this, other)

    /**
     * Calculates the distance to another position.
     *
     * Uses a numerically stable hypotenuse calculation.
     *
     * @param other the other position
     * @return the distance between positions
     * @since 1.0.0
     */
    public fun distanceTo(other: Position3Like<*>): Double = CoordinateMath.distance(this, other)

    /**
     * Calculates the midpoint between this position and another position.
     *
     * @param other the other position
     * @return the midpoint as an immutable position
     * @since 1.0.0
     */
    public fun midpoint(other: Position3Like<*>): P =
        createPosition(
            InterpolationMath.midpoint(x, other.x),
            InterpolationMath.midpoint(y, other.y),
            InterpolationMath.midpoint(z, other.z),
        )

    /**
     * Calculates the displacement from this position to another position.
     *
     * @param other the target position
     * @return the displacement vector
     * @since 1.0.0
     */
    public fun vectorTo(other: Position3Like<*>): Vector3 =
        Vector3(
            other.x - x,
            other.y - y,
            other.z - z,
        )

    /**
     * Adds a displacement to this position.
     *
     * @param vector the displacement
     * @return the resulting immutable position
     * @since 1.0.0
     */
    public operator fun plus(vector: Vector3Like): P =
        createPosition(
            x + vector.x,
            y + vector.y,
            z + vector.z,
        )

    /**
     * Subtracts a displacement from this position.
     *
     * @param vector the displacement
     * @return the resulting immutable position
     * @since 1.0.0
     */
    public operator fun minus(vector: Vector3Like): P =
        createPosition(
            x - vector.x,
            y - vector.y,
            z - vector.z,
        )

    /**
     * Calculates the displacement between this position and another position.
     *
     * @param other the other position
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    public operator fun minus(other: Position3Like<*>): Vector3 =
        Vector3(
            x - other.x,
            y - other.y,
            z - other.z,
        )

    /**
     * Interpolates between this position and another position.
     *
     * Values outside the range [0, 1] produce extrapolated positions.
     *
     * @param other the target position
     * @param progress the interpolation progress
     * @return the resulting immutable position
     * @throws IllegalArgumentException if progress is not finite
     * @since 1.0.0
     */
    public fun lerp(
        other: Position3Like<*>,
        progress: Double,
    ): P {
        NumberValidation.requireFinite(progress, "Progress")

        if (progress == 0.0) {
            return createPosition(x, y, z)
        } else if (progress == 1.0) {
            return createPosition(other.x, other.y, other.z)
        }

        return createPosition(
            InterpolationMath.lerp(x, other.x, progress),
            InterpolationMath.lerp(y, other.y, progress),
            InterpolationMath.lerp(z, other.z, progress),
        )
    }

    /**
     * Checks whether this position represents the origin.
     *
     * @return `true` if all coordinates are zero
     * @since 1.0.0
     */
    public val isOrigin: Boolean
        get() = x == 0.0 && y == 0.0 && z == 0.0
}
