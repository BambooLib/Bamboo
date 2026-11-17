package io.github.deopping.bamboo.geometry.internal.validation

/**
 * Provides validation for three-dimensional coordinates.
 *
 * @author DeOpping
 * @since 1.0.0
 */
internal object CoordinateValidation {
    fun requireFinite(
        x: Double,
        y: Double,
        z: Double,
        valueName: String,
    ) {
        NumberValidation.requireFinite(x, "X $valueName")
        NumberValidation.requireFinite(y, "Y $valueName")
        NumberValidation.requireFinite(z, "Z $valueName")
    }
}
