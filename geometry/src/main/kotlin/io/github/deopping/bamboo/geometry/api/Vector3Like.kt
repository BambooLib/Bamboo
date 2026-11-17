package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.math.CoordinateMath
import io.github.deopping.bamboo.geometry.internal.math.InterpolationMath
import io.github.deopping.bamboo.geometry.internal.math.VectorMath
import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation

/**
 * Provides read-only access to three-dimensional vector components
 * and common vector operations.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface Vector3Like : Coordinate3Like {
    /**
     * Calculates the squared length of this vector.
     *
     * This avoids the square root calculation performed by [length].
     * The result may overflow for very large components.
     *
     * @return the squared vector length
     * @since 1.0.0
     */
    public fun lengthSquared(): Double = CoordinateMath.lengthSquared(this)

    /**
     * Calculates the length of this vector.
     *
     * @return the vector length
     * @since 1.0.0
     */
    public fun length(): Double = CoordinateMath.length(this)

    /**
     * Calculates the dot product with another vector.
     *
     * @param other the other vector
     * @return the dot product
     * @since 1.0.0
     */
    public fun dot(other: Vector3Like): Double = VectorMath.dot(this, other)

    /**
     * Calculates the cross product with another vector.
     *
     * @param other the other vector
     * @return the cross product in an immutable vector
     * @since 1.0.0
     */
    public fun cross(other: Vector3Like): Vector3 = VectorMath.cross(this, other)

    /**
     * Returns a normalized version of this vector.
     *
     * The returned vector is immutable and has a length of one.
     * This operation does not modify the original vector.
     *
     * @return a new normalized immutable vector
     * @throws IllegalArgumentException if this vector has zero length
     * @since 1.0.0
     */
    public fun normalized(): Vector3 {
        val currentX = x
        val currentY = y
        val currentZ = z

        val scale = VectorMath.normalizationScale(currentX, currentY, currentZ)
        val inverseScale = 1.0 / scale

        val scaledX = VectorMath.scaledComponent(currentX, scale, inverseScale)
        val scaledY = VectorMath.scaledComponent(currentY, scale, inverseScale)
        val scaledZ = VectorMath.scaledComponent(currentZ, scale, inverseScale)

        val inverseLength = VectorMath.inverseScaledLength(scaledX, scaledY, scaledZ)

        return Vector3(
            scaledX * inverseLength,
            scaledY * inverseLength,
            scaledZ * inverseLength,
        )
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other the other vector
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    public operator fun plus(other: Vector3Like): Vector3 =
        Vector3(
            x + other.x,
            y + other.y,
            z + other.z,
        )

    /**
     * Subtracts another vector from this vector
     *
     * @param other the vector to subtract
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    public operator fun minus(other: Vector3Like): Vector3 =
        Vector3(
            x - other.x,
            y - other.y,
            z - other.z,
        )

    /**
     * Multiplies this vector by a scalar.
     *
     * @param scalar the scalar multiplier
     * @return the resulting immutable vector
     * @throws IllegalArgumentException if the scalar is not finite
     * @since 1.0.0
     */
    public operator fun times(scalar: Double): Vector3 {
        NumberValidation.requireFinite(scalar, "Scalar")

        return Vector3(
            x * scalar,
            y * scalar,
            z * scalar,
        )
    }

    /**
     * Divides this vector by a scalar.
     *
     * @param scalar the scalar divisor
     * @return the resulting vector
     * @throws IllegalArgumentException if the scalar is zero or non-finite
     * @since 1.0.0
     */
    public operator fun div(scalar: Double): Vector3 {
        NumberValidation.requireFiniteNonZero(scalar, "Scalar")

        return Vector3(
            x / scalar,
            y / scalar,
            z / scalar,
        )
    }

    /**
     * Negates this vector.
     *
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    public operator fun unaryMinus(): Vector3 = Vector3(-x, -y, -z)

    /**
     * Calculates the linear interpolation between this vector and another vector.
     *
     * A progress value of zero returns this vector, while a value of one returns
     * the other vector.
     * Values outside this range are supported and produce extrapolated results.
     *
     * @param other the target vector
     * @param progress the interpolation progress
     * @return the interpolated immutable vector
     * @throws IllegalArgumentException if progress is not finite
     * @since 1.0.0
     */
    public fun lerp(
        other: Vector3Like,
        progress: Double,
    ): Vector3 {
        NumberValidation.requireFinite(progress, "Progress")

        if (progress == 0.0) {
            return Vector3(x, y, z)
        } else if (progress == 1.0) {
            return Vector3(other.x, other.y, other.z)
        }

        return Vector3(
            InterpolationMath.lerp(x, other.x, progress),
            InterpolationMath.lerp(y, other.y, progress),
            InterpolationMath.lerp(z, other.z, progress),
        )
    }

    /**
     * Checks whether all components in this vector are zero.
     *
     * @return `true` if all components are zero
     * @since 1.0.0
     */
    public val isZero: Boolean
        get() = x == 0.0 && y == 0.0 && z == 0.0
}
