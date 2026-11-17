package io.github.deopping.bamboo.geometry.internal.math

import io.github.deopping.bamboo.geometry.api.Rotation
import io.github.deopping.bamboo.geometry.api.Vector3
import io.github.deopping.bamboo.geometry.api.Vector3Like
import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Provides common rotation calculations.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object RotationMath {
    fun normalizeDegrees(degrees: Double): Double {
        NumberValidation.requireFinite(degrees, "Angle")

        return ((degrees % 360.0 + 540.0) % 360.0) - 180.0
    }

    fun interpolateYaw(
        start: Double,
        end: Double,
        progress: Double,
    ): Double {
        val startYaw = normalizeDegrees(start)
        val endYaw = normalizeDegrees(end)
        val difference = normalizeDegrees(endYaw - startYaw)

        return normalizeDegrees(startYaw + difference * progress)
    }

    fun toDirection(
        yaw: Double,
        pitch: Double,
    ): Vector3 {
        val yawRadians = Math.toRadians(yaw)
        val pitchRadians = Math.toRadians(pitch)
        val cosPitch = cos(pitchRadians)

        return Vector3(
            -sin(yawRadians) * cosPitch,
            -sin(pitchRadians),
            cos(yawRadians) * cosPitch,
        )
    }

    fun fromDirection(direction: Vector3Like): Rotation {
        val normalized = direction.normalized()

        return Rotation(
            yaw = Math.toDegrees(atan2(-normalized.x, normalized.z)),
            pitch = Math.toDegrees(asin(-normalized.y.coerceIn(-1.0, 1.0))),
        )
    }
}
