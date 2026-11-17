package io.github.deopping.bamboo.geometry.internal.math

import io.github.deopping.bamboo.geometry.api.Coordinate3Like
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Provides common three-dimensional coordinate calculations.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object CoordinateMath {
    fun lengthSquared(
        x: Double,
        y: Double,
        z: Double,
    ): Double = x * x + y * y + z * z

    fun lengthSquared(coordinates: Coordinate3Like): Double = lengthSquared(coordinates.x, coordinates.y, coordinates.z)

    fun length(
        x: Double,
        y: Double,
        z: Double,
    ): Double = hypot(hypot(x, y), z)

    fun length(coordinates: Coordinate3Like): Double = length(coordinates.x, coordinates.y, coordinates.z)

    fun distanceSquared(
        xOne: Double,
        yOne: Double,
        zOne: Double,
        xTwo: Double,
        yTwo: Double,
        zTwo: Double,
    ): Double =
        lengthSquared(
            xTwo - xOne,
            yTwo - yOne,
            zTwo - zOne,
        )

    fun distanceSquared(
        xOne: Int,
        yOne: Int,
        zOne: Int,
        xTwo: Int,
        yTwo: Int,
        zTwo: Int,
    ): Double =
        distanceSquared(
            xOne.toDouble(),
            yOne.toDouble(),
            zOne.toDouble(),
            xTwo.toDouble(),
            yTwo.toDouble(),
            zTwo.toDouble(),
        )

    fun distanceSquared(
        first: Coordinate3Like,
        second: Coordinate3Like,
    ): Double =
        distanceSquared(
            first.x,
            first.y,
            first.z,
            second.x,
            second.y,
            second.z,
        )

    fun distance(
        xOne: Double,
        yOne: Double,
        zOne: Double,
        xTwo: Double,
        yTwo: Double,
        zTwo: Double,
    ): Double =
        length(
            xTwo - xOne,
            yTwo - yOne,
            zTwo - zOne,
        )

    fun distance(
        xOne: Int,
        yOne: Int,
        zOne: Int,
        xTwo: Int,
        yTwo: Int,
        zTwo: Int,
    ): Double =
        distance(
            xOne.toDouble(),
            yOne.toDouble(),
            zOne.toDouble(),
            xTwo.toDouble(),
            yTwo.toDouble(),
            zTwo.toDouble(),
        )

    fun distance(
        first: Coordinate3Like,
        second: Coordinate3Like,
    ): Double =
        distance(
            first.x,
            first.y,
            first.z,
            second.x,
            second.y,
            second.z,
        )

    fun maxAbsComponent(
        x: Double,
        y: Double,
        z: Double,
    ): Double = maxOf(abs(x), abs(y), abs(z))

    fun maxAbsComponent(coordinates: Coordinate3Like): Double =
        maxAbsComponent(coordinates.x, coordinates.y, coordinates.z)
}
