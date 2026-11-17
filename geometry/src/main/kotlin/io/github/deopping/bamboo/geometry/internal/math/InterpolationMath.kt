package io.github.deopping.bamboo.geometry.internal.math

/**
 * Provides common interpolation calculation.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object InterpolationMath {
    fun midpoint(
        first: Double,
        second: Double,
    ): Double = first * 0.5 + second * 0.5

    fun lerp(
        start: Double,
        end: Double,
        progress: Double,
    ): Double = start + (end - start) * progress
}
