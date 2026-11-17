package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.math.RotationMath
import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation

/**
 * Represents a mutable Minecraft-style rotation.
 *
 * Angles are expressed in degrees.
 *
 * Mutable rotations use reference equality and are not thread-safe.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class MutableRotation(
    yaw: Double,
    pitch: Double,
) : RotationLike {
    private var yawValue: Double = yaw
    private var pitchValue: Double = pitch
    private var directionCache: Vector3? = null

    /**
     * Gets or sets the yaw angle.
     * @since 1.0.0
     */
    public override var yaw: Double
        get() = yawValue
        set(value) {
            NumberValidation.requireFinite(value, "Yaw")

            yawValue = value
            directionCache = null
        }

    /**
     * Gets or sets the pitch angle.
     * @since 1.0.0
     */
    public override var pitch: Double
        get() = pitchValue
        set(value) {
            NumberValidation.requireFiniteAndBetween(value, -90.0, 90.0, "Pitch")

            pitchValue = value
            directionCache = null
        }

    init {
        NumberValidation.requireFinite(yaw, "Yaw")
        NumberValidation.requireFiniteAndBetween(pitch, -90.0, 90.0, "Pitch")
    }

    /**
     * Replaces both rotation angles.
     *
     * Both values are validated before modifying this rotation.
     *
     * @param yaw the new yaw angle
     * @param pitch the new pitch angle
     * @return this mutable rotation
     * @since 1.0.0
     */
    public fun set(
        yaw: Double,
        pitch: Double,
    ): MutableRotation {
        NumberValidation.requireFinite(yaw, "Yaw")
        NumberValidation.requireFiniteAndBetween(pitch, -90.0, 90.0, "Pitch")

        yawValue = yaw
        pitchValue = pitch
        directionCache = null

        return this
    }

    /**
     * Copies the angles from another rotation.
     *
     * @param other the rotation to copy
     * @return this mutable rotation
     * @since 1.0.0
     */
    public fun set(other: RotationLike): MutableRotation = set(other.yaw, other.pitch)

    /**
     * Adds rotation angles in place.
     *
     * @param yaw the yaw adjustment in degrees
     * @param pitch the pitch adjustment in degrees
     * @return this mutable rotation
     * @since 1.0.0
     */
    public fun rotate(
        yaw: Double,
        pitch: Double,
    ): MutableRotation {
        NumberValidation.requireFinite(yaw, "Yaw")
        NumberValidation.requireFinite(pitch, "Pitch")

        return set(
            this.yaw + yaw,
            this.pitch + pitch,
        )
    }

    /**
     * Normalizes the yaw angle in place.
     *
     * @return this mutable rotation
     * @since 1.0.0
     */
    public fun normalize(): MutableRotation {
        yaw = RotationMath.normalizeDegrees(yaw)
        return this
    }

    /**
     * Interpolates this rotation toward another rotation in place.
     *
     * Yaw follows the shortest angular path.
     *
     * @param other the target rotation
     * @param progress the interpolation progress between zero and one
     * @return this mutable rotation
     * @throws IllegalArgumentException if progress is outside the range [0, 1]
     * or is non-finite
     * @since 1.0.0
     */
    public fun lerpAssign(
        other: RotationLike,
        progress: Double,
    ): MutableRotation {
        NumberValidation.requireFiniteAndBetween(progress, 0.0, 1.0, "Progress")

        return set(
            yaw = RotationMath.interpolateYaw(yaw, other.yaw, progress),
            pitch = pitch + (other.pitch - pitch) * progress,
        )
    }

    /**
     * Converts this rotation into a normalized direction vector.
     *
     * Uses cached trigonometric values.
     *
     * @return the direction represented by this rotation
     * @since 1.0.0
     */
    public override fun toDirection(): Vector3 {
        directionCache?.let { return it }

        val direction = RotationMath.toDirection(yaw, pitch)

        directionCache = direction

        return direction
    }

    /**
     * Converts this rotation into an immutable rotation.
     *
     * The returned rotation is an independent snapshot.
     *
     * @return a new immutable rotation
     * @since 1.0.0
     */
    public fun toImmutable(): Rotation = Rotation(yaw, pitch)
}
