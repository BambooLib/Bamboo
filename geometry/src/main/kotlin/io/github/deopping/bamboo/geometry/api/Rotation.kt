package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.GeometryMath
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
        GeometryMath.requireFinite(yaw, "Yaw")

        require(pitch.isFinite() && pitch in -90.0..90.0) {
            "Pitch must be finite and between -90 and 90 degrees."
        }
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

        val yawRadians = Math.toRadians(yaw)
        val pitchRadians = Math.toRadians(pitch)

        val cosPitch = cos(pitchRadians)

        val direction =
            Vector3(
                x = -sin(yawRadians) * cosPitch,
                y = -sin(pitchRadians),
                z = cos(yawRadians) * cosPitch,
            )

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
        public fun fromDirection(direction: Vector3Like): Rotation {
            val normalized = direction.normalized()

            return Rotation(
                yaw = Math.toDegrees(atan2(-normalized.x, normalized.z)),
                pitch = Math.toDegrees(asin(-normalized.y.coerceIn(-1.0, 1.0))),
            )
        }
    }
}
