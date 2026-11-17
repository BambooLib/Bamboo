package io.github.deopping.bamboo.geometry.internal.math

import io.github.deopping.bamboo.geometry.api.Coordinate3Like
import io.github.deopping.bamboo.geometry.api.Vector3

/**
 * Provides common vector calculation.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object VectorMath {
    fun dot(
        xOne: Double,
        yOne: Double,
        zOne: Double,
        xTwo: Double,
        yTwo: Double,
        zTwo: Double,
    ): Double = xOne * xTwo + yOne * yTwo + zOne * zTwo

    fun dot(
        first: Coordinate3Like,
        second: Coordinate3Like,
    ): Double =
        dot(
            first.x,
            first.y,
            first.z,
            second.x,
            second.y,
            second.z,
        )

    fun cross(
        xOne: Double,
        yOne: Double,
        zOne: Double,
        xTwo: Double,
        yTwo: Double,
        zTwo: Double,
    ): Vector3 =
        Vector3(
            yOne * zTwo - zOne * yTwo,
            zOne * xTwo - xOne * zTwo,
            xOne * yTwo - yOne * xTwo,
        )

    fun cross(
        first: Coordinate3Like,
        second: Coordinate3Like,
    ): Vector3 =
        cross(
            first.x,
            first.y,
            first.z,
            second.x,
            second.y,
            second.z,
        )

    fun normalizationScale(
        x: Double,
        y: Double,
        z: Double,
    ): Double {
        val scale = CoordinateMath.maxAbsComponent(x, y, z)

        require(scale > 0.0) {
            "Cannot normalize a zero-length vector."
        }

        return scale
    }

    fun normalizationScale(coordinates: Coordinate3Like): Double =
        normalizationScale(coordinates.x, coordinates.y, coordinates.z)

    fun scaledComponent(
        component: Double,
        scale: Double,
        inverseScale: Double,
    ): Double =
        if (inverseScale.isFinite()) {
            component * inverseScale
        } else {
            component / scale
        }

    fun inverseScaledLength(
        x: Double,
        y: Double,
        z: Double,
    ): Double = 1.0 / CoordinateMath.length(x, y, z)

    fun inverseScaledLength(coordinates: Coordinate3Like): Double =
        inverseScaledLength(coordinates.x, coordinates.y, coordinates.z)
}
