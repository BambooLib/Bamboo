package io.github.deopping.bamboo.geometry.internal

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Provides internal mathematical and validation utilities for Bamboo Geometry.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object GeometryMath {
    /**
     * Requires a value to be finite.
     *
     * @param value the value to validate
     * @param name the descriptive name of the value
     * @since 1.0.0
     */
    internal fun requireFinite(
        value: Double,
        name: String,
    ) {
        require(value.isFinite()) {
            "$name must be finite."
        }
    }

    /**
     * Requires three coordinate values to be finite.
     *
     * @param x the X component
     * @param y the Y component
     * @param z the Z component
     * @param type the type of coordinate being validated
     * @since 1.0.0
     */
    internal fun requireFinite3(
        x: Double,
        y: Double,
        z: Double,
        type: String,
    ) {
        require(x.isFinite()) { "X $type must be finite." }
        require(y.isFinite()) { "Y $type must be finite." }
        require(z.isFinite()) { "Z $type must be finite." }
    }

    /**
     * Calculates a scale suitable for safely normalizing a three-dimensional vector.
     *
     * @param x the X component
     * @param y the Y component
     * @param z the Z component
     * @return the largest absolute component
     * @throws IllegalArgumentException if all components are zero
     * @since 1.0.0
     */
    internal fun normalizationScale(
        x: Double,
        y: Double,
        z: Double,
    ): Double {
        val scale = maxOf(abs(x), abs(y), abs(z))

        require(scale > 0.0) {
            "Cannot normalize a zero-length vector."
        }

        return scale
    }

    /**
     * Calculates the length of a vector from its scaled components.
     *
     * @param scaledX the scaled X component
     * @param scaledY the scaled Y component
     * @param scaledZ the scaled Z component
     * @return the scaled vector length
     * @since 1.0.0
     */
    internal fun scaledLength(
        scaledX: Double,
        scaledY: Double,
        scaledZ: Double,
    ): Double =
        sqrt(
            scaledX * scaledX +
                scaledY * scaledY +
                scaledZ * scaledZ,
        )

    /**
     * Converts a finite coordinate to its containing block coordinate.
     *
     * The coordinate is rounded down rather than truncated toward zero.
     *
     * @param value the coordinate to convert
     * @param name the descriptive name of the coordinate
     * @return the floored block coordinate
     * @throws IllegalArgumentException if the value is non-finite or outside the Int range
     * @since 1.0.0
     */
    internal fun floorToInt(
        value: Double,
        name: String,
    ): Int {
        requireFinite(value, name)

        val floored = floor(value)

        require(floored >= Int.MIN_VALUE.toDouble() && floored <= Int.MAX_VALUE.toDouble()) {
            "$name is outside the Int coordinate range."
        }

        return floored.toInt()
    }
}
