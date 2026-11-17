package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.math.InterpolationMath
import io.github.deopping.bamboo.geometry.internal.math.VectorMath
import io.github.deopping.bamboo.geometry.internal.validation.CoordinateValidation
import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation
import io.github.deopping.bamboo.geometry.internal.validation.VectorValidation
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Represents a mutable three-dimensional vector.
 *
 * Mutating this vector does not create a new vector instance.
 *
 * Mutable vectors use reference equality. Convert to [Vector3] when
 * value-based equality or an immutable snapshot is required.
 *
 * Mutable vectors are not thread-safe. External synchronization is required
 * when sharing instances between threads.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class MutableVector3(
    x: Double,
    y: Double,
    z: Double,
) : Vector3Like {
    private var xComponent: Double = x
    private var yComponent: Double = y
    private var zComponent: Double = z

    /**
     * Gets or sets the X component.
     * @since 1.0.0
     */
    public override var x: Double
        get() = xComponent
        set(value) {
            NumberValidation.requireFinite(value, "X coordinate")
            xComponent = value
        }

    /**
     * Gets or sets the Y component.
     * @since 1.0.0
     */
    public override var y: Double
        get() = yComponent
        set(value) {
            NumberValidation.requireFinite(value, "Y coordinate")
            yComponent = value
        }

    /**
     * Gets or sets the Z component.
     * @since 1.0.0
     */
    public override var z: Double
        get() = zComponent
        set(value) {
            NumberValidation.requireFinite(value, "Z coordinate")
            zComponent = value
        }

    init {
        CoordinateValidation.requireFinite(x, y, z, "coordinate")
    }

    /**
     * Replaces all vector components.
     *
     * All components are validated before any modification occurs.
     *
     * @param x the new X component
     * @param y the new Y component
     * @param z the new Z component
     * @return this mutable vector
     * @since 1.0.0
     */
    public fun set(
        x: Double,
        y: Double,
        z: Double,
    ): MutableVector3 {
        CoordinateValidation.requireFinite(x, y, z, "coordinate")

        xComponent = x
        yComponent = y
        zComponent = z

        return this
    }

    /**
     * Copies the components from another vector.
     *
     * @param other the vector to copy
     * @return this mutable vector
     * @since 1.0.0
     */
    public fun set(other: Vector3Like): MutableVector3 = set(other.x, other.y, other.z)

    /**
     * Adds another vector to this vector in place.
     *
     * @param other the vector to add
     * @since 1.0.0
     */
    public operator fun plusAssign(other: Vector3Like) {
        set(
            x + other.x,
            y + other.y,
            z + other.z,
        )
    }

    /**
     * Subtracts another vector from this vector in place.
     *
     * @param other the vector to subtract
     * @since 1.0.0
     */
    public operator fun minusAssign(other: Vector3Like) {
        set(
            x - other.x,
            y - other.y,
            z - other.z,
        )
    }

    /**
     * Multiplies this vector by a scalar in place.
     *
     * @param scalar the scalar multiplier
     * @since 1.0.0
     */
    public operator fun timesAssign(scalar: Double) {
        NumberValidation.requireFinite(scalar, "Scalar")

        set(
            x * scalar,
            y * scalar,
            z * scalar,
        )
    }

    /**
     * Divides this vector by a scalar in place.
     *
     * @param scalar the scalar divisor
     * @throws IllegalArgumentException if the divisor is zero or non-finite
     * @since 1.0.0
     */
    public operator fun divAssign(scalar: Double) {
        NumberValidation.requireFiniteNonZero(scalar, "Scalar")

        set(
            x / scalar,
            y / scalar,
            z / scalar,
        )
    }

    /**
     * Normalizes this vector in place.
     *
     * @return this mutable vector
     * @throws IllegalArgumentException if this vector has zero length
     * @since 1.0.0
     */
    public fun normalize(): MutableVector3 {
        val currentX = x
        val currentY = y
        val currentZ = z

        val scale = VectorMath.normalizationScale(currentX, currentY, currentZ)
        val inverseScale = 1.0 / scale

        val scaledX = VectorMath.scaledComponent(currentX, scale, inverseScale)
        val scaledY = VectorMath.scaledComponent(currentY, scale, inverseScale)
        val scaledZ = VectorMath.scaledComponent(currentZ, scale, inverseScale)

        val inverseLength = VectorMath.inverseScaledLength(scaledX, scaledY, scaledZ)

        xComponent = scaledX * inverseLength
        yComponent = scaledY * inverseLength
        zComponent = scaledZ * inverseLength

        return this
    }

    /**
     * Interpolates this vector toward another vector in place.
     *
     * A progress value of zero leaves this vector unchanged, while a value
     * of one replaces it with the target vector.
     *
     * Values outside this range are supported and produce extrapolated results.
     *
     * @param other the target vector
     * @param progress the interpolation progress
     * @return this mutable vector
     * @throws IllegalArgumentException if progress is not finite
     * @since 1.0.0
     */
    public fun lerpAssign(
        other: Vector3Like,
        progress: Double,
    ): MutableVector3 {
        NumberValidation.requireFinite(progress, "Progress")

        if (progress == 0.0) {
            return this
        } else if (progress == 1.0) {
            return set(other.x, other.y, other.z)
        }

        return set(
            InterpolationMath.lerp(x, other.x, progress),
            InterpolationMath.lerp(y, other.y, progress),
            InterpolationMath.lerp(z, other.z, progress),
        )
    }

    /**
     * Calculates the cross product with another vector in place
     *
     * @param other the other vector
     * @return this mutable vector
     * @since 1.0.0
     */
    public fun crossAssign(other: Vector3Like): MutableVector3 =
        set(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x,
        )

    /**
     * Negates this vector in place.
     *
     * @return this mutable vector
     * @since 1.0.0
     */
    public fun negate(): MutableVector3 = set(-x, -y, -z)

    /**
     * Sets the length of this vector without changing its direction.
     *
     * @param length the desired length
     * @return this mutable vector
     * @throws IllegalArgumentException if the length is negative or non-finite,
     * or if a zero-length vector is assigned a non-zero length
     * @since 1.0.0
     */
    public fun setLength(length: Double): MutableVector3 {
        NumberValidation.requireFiniteNonNegative(length, "Length")

        if (length == 0.0) {
            return set(0.0, 0.0, 0.0)
        }

        val scale = maxOf(abs(x), abs(y), abs(z))

        VectorValidation.requirePositiveScale(scale)

        val scaleInverse = 1.0 / scale

        val scaledX: Double
        val scaledY: Double
        val scaledZ: Double

        if (scaleInverse.isFinite()) {
            scaledX = x * scaleInverse
            scaledY = y * scaleInverse
            scaledZ = z * scaleInverse
        } else {
            scaledX = x / scale
            scaledY = y / scale
            scaledZ = z / scale
        }

        val currentLengthInverse =
            1.0 /
                hypot(
                    hypot(scaledX, scaledY),
                    scaledZ,
                )

        return set(
            scaledX * currentLengthInverse * length,
            scaledY * currentLengthInverse * length,
            scaledZ * currentLengthInverse * length,
        )
    }

    /**
     * Converts this vector into an immutable vector.
     *
     * The returned vector is an independent snapshot.
     *
     * @return a new immutable vector
     * @since 1.0.0
     */
    public fun toImmutable(): Vector3 = Vector3(x, y, z)
}
