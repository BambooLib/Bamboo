package io.github.deopping.bamboo.geometry.internal.math

import io.github.deopping.bamboo.geometry.internal.validation.NumberValidation
import kotlin.math.floor

/**
 * Provides common block-coordinate calculations.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object BlockCoordinateMath {
    fun floorToInt(
        value: Double,
        coordinateName: String,
    ): Int {
        NumberValidation.requireFinite(value, coordinateName)

        val floored = floor(value)

        require(floored >= Int.MIN_VALUE.toDouble() && floored <= Int.MAX_VALUE.toDouble()) {
            "$coordinateName must be within the Int range after flooring."
        }

        return floored.toInt()
    }
}
