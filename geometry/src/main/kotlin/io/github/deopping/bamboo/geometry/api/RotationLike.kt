package io.github.deopping.bamboo.geometry.api

import kotlin.math.cos
import kotlin.math.sin

/**
 * Provides read-only access to yaw and pitch and common rotation operations.
 *
 * Angles are expressed in degrees.
 *
 * Yaw zero faces positive Z, with positive yaw rotation toward negative X.
 * Positive pitch points downward.
 *
 * Implementations must provide finite yaw values and finite pitch values
 * within the range [-90, 90] degrees.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface RotationLike {
    /**
     * Gets the yaw angle.
     * @since 1.0.0
     */
    public val yaw: Double

    /**
     * Gets the pitch angle.
     * @since 1.0.0
     */
    public val pitch: Double

    /**
     * Converts this rotation into a normalized direction vector.
     *
     * @return the direction represented by this rotation
     * @since 1.0.0
     */
    public fun toDirection(): Vector3 {
        val yawRadians = Math.toRadians(yaw)
        val pitchRadians = Math.toRadians(pitch)

        val cosPitch = cos(pitchRadians)

        return Vector3(
            x = -sin(yawRadians) * cosPitch,
            y = -sin(pitchRadians),
            z = cos(yawRadians) * cosPitch,
        )
    }

    /**
     * Returns a normalized version of this rotation.
     *
     * The yaw angle is normalized to the range [-180, 180).
     * Pitch is preserved.
     *
     * @return a new immutable normalized rotation
     * @since 1.0.0
     */
    public fun normalized(): Rotation =
        Rotation(
            yaw = normalizeDegrees(yaw),
            pitch = pitch,
        )

    /**
     * Interpolates between this rotation and another rotation.
     *
     * Yaw interpolation follows the shortest angular path.
     * Pitch is interpolated linearly.
     *
     * @param other the target rotation
     * @param progress the interpolation progress, between zero and one
     * @return the interpolated immutable rotation
     * @throws IllegalArgumentException if progress is outside the range [0, 1]
     * or is non-finite
     * @since 1.0.0
     */
    public fun lerp(
        other: RotationLike,
        progress: Double,
    ): Rotation {
        require(progress.isFinite() && progress in 0.0..1.0) {
            "Progress must be finite and between zero and one."
        }

        val startYaw = normalizeDegrees(yaw)
        val endYaw = normalizeDegrees(other.yaw)
        val yawDifference = normalizeDegrees(endYaw - startYaw)

        return Rotation(
            yaw = normalizeDegrees(startYaw + yawDifference * progress),
            pitch = pitch + (other.pitch - pitch) * progress,
        )
    }

    public companion object {
        /**
         * Normalizes an angle to the range [-180, 180).
         *
         * @param degrees the angle in degrees
         * @return the normalized angle
         * @since 1.0.0
         */
        public fun normalizeDegrees(degrees: Double): Double {
            require(degrees.isFinite()) {
                "Angle must be finite."
            }

            return ((degrees % 360.0 + 540.0) % 360.0) - 180.0
        }
    }
}
