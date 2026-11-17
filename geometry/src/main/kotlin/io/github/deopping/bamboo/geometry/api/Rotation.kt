package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.math.RotationMath
import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation

/**
 * Represents an immutable Minecraft-style rotation.
 *
 * Angles are expressed in degrees.
 *
 * @property yaw the horizontal rotation
 * @property pitch the vertical rotation
 * @author DeOpping
 * @since 1.0.0
 */
public data class Rotation(
    public override val yaw: Double,
    public override val pitch: Double,
) : RotationLike {
    @Volatile
    private var directionCache: Vector3? = null

    init {
        NumberValidation.requireFinite(yaw, "Yaw")
        NumberValidation.requireFiniteAndBetween(pitch, -90.0, 90.0, "Pitch")
    }

    /**
     * Converts this rotation into a mutable rotation.
     *
     * @return a new mutable rotation with the same angles
     * @since 1.0.0
     */
    public fun toMutable(): MutableRotation = MutableRotation(yaw, pitch)

    /**
     * Converts this rotation into a normalized direction vector.
     *
     * The resulting direction is cached after its first calculation.
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
     * Contains constants and factory methods for immutable rotations.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Represents zero yaw and pitch.
         * @since 1.0.0
         */
        public val ZERO: Rotation = Rotation(0.0, 0.0)

        /**
         * Creates a rotation from a direction vector.
         *
         * The direction must have a non-zero length.
         *
         * @param direction the direction to convert
         * @return the corresponding rotation
         * @throws IllegalArgumentException if the direction has zero length
         * @since 1.0.0
         */
        public fun fromDirection(direction: Vector3Like): Rotation = RotationMath.fromDirection(direction)
    }
}
